package zentry.back.api.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import zentry.back.api.core.dtos.InviteMemberRequest;
import zentry.back.api.core.dtos.NoteRequestDTO;
import zentry.back.api.core.dtos.ProjectLikeResponse;
import zentry.back.api.core.dtos.ProjectMemberResponse;
import zentry.back.api.core.dtos.ProjectRequestDTO;
import zentry.back.api.core.dtos.ResourceRequestDTO;
import zentry.back.api.core.dtos.TaskRequestDTO;
import zentry.back.api.core.models.*;
import zentry.back.api.core.repositories.ProjectLikeRepository;
import zentry.back.api.core.repositories.ProjectMemberRepository;
import zentry.back.api.core.repositories.ProjectRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.global.mappers;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectLikeRepository projectLikeRepository;
    private final UserRepository userRepository;
    private final GamificationEventService gamificationEventService;

    // 1. Obtener todos los proyectos del usuario (propios + donde es colaborador)
    public List<Project> getProjectsByUser(String username) {
        String cleanUser = requireUsername(username);

        Set<Long> projectIds = new LinkedHashSet<>();
        List<Project> owned = projectRepository.findByCreatedByOrderByUpdatedAtDesc(cleanUser);
        if (owned.isEmpty() && cleanUser.contains("@")) {
            owned = projectRepository.findByCreatedByOrderByUpdatedAtDesc(cleanUser.split("@")[0]);
        }
        owned.forEach(p -> projectIds.add(p.getId()));

        List<Project> result = new java.util.ArrayList<>(owned);
        projectMemberRepository.findByUsername(cleanUser).stream()
                .map(ProjectMember::getProjectId)
                .filter(id -> !projectIds.contains(id))
                .forEach(id -> projectRepository.findById(id).ifPresent(result::add));

        return result;
    }

    // 2. Obtener un proyecto por ID (dueño o colaborador)
    public Project getProjectById(Long id, String username) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(id);
        assertAccess(project, cleanUser);
        return project;
    }

    // 3. Crear nuevo proyecto
    @Transactional
    public Project createProject(String username, ProjectRequestDTO dto) {
        String cleanUser = requireUsername(username);

        Project project = Project.builder()
                .title(dto.getTitle() != null && !dto.getTitle().isBlank() ? dto.getTitle() : "Nuevo Proyecto")
                .description(dto.getDescription())
                .category(dto.getCategory() != null ? dto.getCategory() : "UI/UX")
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .status(dto.getStatus() != null ? dto.getStatus() : "active")
                .deadline(dto.getDeadline() != null ? dto.getDeadline() : "Sin fecha límite")
                .createdBy(cleanUser)
                .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : "")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        String safeAvatar = cleanUser.length() >= 2 ? cleanUser.substring(0, 2).toUpperCase() : "ZN";

        // Agregar actividad inicial
        ProjectActivity initAct = ProjectActivity.builder()
                .user(cleanUser)
                .avatar(safeAvatar)
                .action("creó el proyecto")
                .target(project.getTitle())
                .iconType("member")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(initAct);
        Project saved = projectRepository.save(project);

        projectMemberRepository.save(ProjectMember.builder()
                .projectId(saved.getId())
                .username(cleanUser)
                .role("OWNER")
                .joinedAt(LocalDateTime.now())
                .build());

        userRepository.findByEmail(cleanUser).ifPresent(u ->
                gamificationEventService.recordMissionProgress(u.getId(), "create_project", 1));

        return saved;
    }

    // 4. Actualizar Proyecto
    @Transactional
    public Project updateProject(Long id, String username, ProjectRequestDTO dto) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(id);
        assertOwnership(project, cleanUser);

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) project.setTitle(dto.getTitle());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getCategory() != null) project.setCategory(dto.getCategory());
        if (dto.getPriority() != null) project.setPriority(dto.getPriority());
        if (dto.getStatus() != null) project.setStatus(dto.getStatus());
        if (dto.getDeadline() != null) project.setDeadline(dto.getDeadline());
        if (dto.getTags() != null) project.setTags(String.join(",", dto.getTags()));

        project.setUpdatedAt(LocalDateTime.now());

        String safeAvatar = cleanUser.length() >= 2 ? cleanUser.substring(0, 2).toUpperCase() : "ZN";

        ProjectActivity act = ProjectActivity.builder()
                .user(cleanUser)
                .avatar(safeAvatar)
                .action("actualizó el proyecto")
                .target(project.getTitle())
                .iconType("status")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(act);
        return projectRepository.save(project);
    }

    // 5. Eliminar Proyecto
    @Transactional
    public void deleteProject(Long id, String username) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(id);
        assertOwnership(project, cleanUser);
        projectMemberRepository.deleteByProjectId(id);
        projectLikeRepository.deleteByProjectId(id);
        projectRepository.delete(project);
    }

    // 6. Agregar Tarea
    @Transactional
    public ProjectTask addTask(Long projectId, String username, TaskRequestDTO dto) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertAccess(project, cleanUser);

        ProjectTask task = ProjectTask.builder()
                .title(dto.getTitle() != null ? dto.getTitle() : "Nueva Tarea")
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .assignedTo(dto.getAssignedTo() != null ? dto.getAssignedTo() : cleanUser)
                .dueDate(dto.getDueDate() != null ? dto.getDueDate() : "Pronto")
                .completed(false)
                .project(project)
                .build();

        project.getTasks().add(task);
        project.setUpdatedAt(LocalDateTime.now());

        String safeAvatar = cleanUser.length() >= 2 ? cleanUser.substring(0, 2).toUpperCase() : "ZN";

        ProjectActivity act = ProjectActivity.builder()
                .user(cleanUser)
                .avatar(safeAvatar)
                .action("creó la tarea")
                .target(task.getTitle())
                .iconType("task")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(act);
        Project saved = projectRepository.save(project);
        return saved.getTasks().get(saved.getTasks().size() - 1);
    }

    // 7. Toggle Tarea
    @Transactional
    public ProjectTask toggleTask(Long projectId, Long taskId, String username) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertAccess(project, cleanUser);

        ProjectTask task = project.getTasks().stream()
                .filter(t -> t.getId() != null && t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));

        task.setCompleted(!task.isCompleted());
        project.setUpdatedAt(LocalDateTime.now());

        String safeAvatar = cleanUser.length() >= 2 ? cleanUser.substring(0, 2).toUpperCase() : "ZN";

        ProjectActivity act = ProjectActivity.builder()
                .user(cleanUser)
                .avatar(safeAvatar)
                .action(task.isCompleted() ? "completó la tarea" : "reabrió la tarea")
                .target(task.getTitle())
                .iconType("task")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(act);
        projectRepository.save(project);
        return task;
    }

    // 8. Eliminar Tarea
    @Transactional
    public void deleteTask(Long projectId, Long taskId, String username) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertAccess(project, cleanUser);

        project.getTasks().removeIf(t -> t.getId() != null && t.getId().equals(taskId));
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
    }

    // 9. Agregar Recurso
    @Transactional
    public ProjectResource addResource(Long projectId, String username, ResourceRequestDTO dto) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertAccess(project, cleanUser);

        ProjectResource res = ProjectResource.builder()
                .name(dto.getName() != null ? dto.getName() : "Archivo")
                .type(dto.getType() != null ? dto.getType() : "PDF")
                .size(dto.getSize() != null ? dto.getSize() : "1.0 MB")
                .url(dto.getUrl())
                .uploadedBy(cleanUser)
                .uploadedAt(LocalDateTime.now())
                .project(project)
                .build();

        project.getResources().add(res);
        project.setUpdatedAt(LocalDateTime.now());

        String safeAvatar = cleanUser.length() >= 2 ? cleanUser.substring(0, 2).toUpperCase() : "ZN";
        ProjectActivity act = ProjectActivity.builder()
                .user(cleanUser)
                .avatar(safeAvatar)
                .action("subió el recurso")
                .target(res.getName())
                .iconType("file")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(act);
        Project saved = projectRepository.save(project);
        return saved.getResources().get(saved.getResources().size() - 1);
    }

    // 10. Agregar Nota
    @Transactional
    public ProjectNote addNote(Long projectId, String username, NoteRequestDTO dto) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertAccess(project, cleanUser);

        ProjectNote note = ProjectNote.builder()
                .content(dto.getContent() != null ? dto.getContent() : "")
                .author(cleanUser)
                .createdAt(LocalDateTime.now())
                .project(project)
                .build();

        project.getNotes().add(note);
        project.setUpdatedAt(LocalDateTime.now());
        Project saved = projectRepository.save(project);
        return saved.getNotes().get(saved.getNotes().size() - 1);
    }

    // 11. Búsqueda de proyectos (acotada a los proyectos del usuario autenticado)
    public List<Project> searchProjects(String username, String query) {
        String cleanUser = requireUsername(username);
        return projectRepository.searchProjectsByUser(query, cleanUser);
    }

    // 12. Listar colaboradores de un proyecto
    public List<ProjectMemberResponse> listMembers(Long projectId, String username) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertAccess(project, cleanUser);
        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(mappers::toResponse)
                .collect(Collectors.toList());
    }

    // 13. Invitar colaborador (solo el dueño)
    @Transactional
    public ProjectMemberResponse inviteMember(Long projectId, String ownerUsername, InviteMemberRequest request) {
        String cleanOwner = requireUsername(ownerUsername);
        Project project = findProjectOrThrow(projectId);
        assertOwnership(project, cleanOwner);

        String target = request.getUsername().trim();
        User targetUser = userRepository.findByUsernameOrEmail(target, target)
                .or(() -> userRepository.findByEmail(target))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + target));
        if (projectMemberRepository.existsByProjectIdAndUsername(projectId, target)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ese usuario ya es colaborador del proyecto");
        }

        ProjectMember member = projectMemberRepository.save(ProjectMember.builder()
                .projectId(projectId)
                .username(target)
                .role("COLLABORATOR")
                .joinedAt(LocalDateTime.now())
                .build());

        String safeAvatar = cleanOwner.length() >= 2 ? cleanOwner.substring(0, 2).toUpperCase() : "ZN";
        project.getActivities().add(ProjectActivity.builder()
                .user(cleanOwner)
                .avatar(safeAvatar)
                .action("invitó a colaborar a")
                .target(target)
                .iconType("member")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build());
        projectRepository.save(project);

        gamificationEventService.recordAchievementProgress(targetUser.getId(), "project_collab", 1);

        return mappers.toResponse(member);
    }

    // 14. Salir del proyecto o quitar a un colaborador (el dueño no puede ser removido)
    @Transactional
    public void removeMember(Long projectId, String requesterUsername, String targetUsername) {
        String cleanRequester = requireUsername(requesterUsername);
        Project project = findProjectOrThrow(projectId);

        boolean isSelf = cleanRequester.equals(targetUsername);
        if (!isSelf) {
            assertOwnership(project, cleanRequester);
        } else {
            assertAccess(project, cleanRequester);
        }

        if (targetUsername.equals(project.getCreatedBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El dueño del proyecto no puede ser removido");
        }

        ProjectMember member = projectMemberRepository.findByProjectIdAndUsername(projectId, targetUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Colaborador no encontrado"));
        projectMemberRepository.delete(member);
    }

    // 15. Alternar "me gusta" en un proyecto
    @Transactional
    public ProjectLikeResponse toggleLike(Long projectId, String username) {
        String cleanUser = requireUsername(username);
        findProjectOrThrow(projectId);

        boolean liked;
        var existing = projectLikeRepository.findByProjectIdAndUsername(projectId, cleanUser);
        if (existing.isPresent()) {
            projectLikeRepository.delete(existing.get());
            liked = false;
        } else {
            projectLikeRepository.save(ProjectLike.builder()
                    .projectId(projectId)
                    .username(cleanUser)
                    .createdAt(LocalDateTime.now())
                    .build());
            liked = true;
            userRepository.findByEmail(cleanUser).ifPresent(u ->
                    gamificationEventService.recordMissionProgress(u.getId(), "react_projects", 1));
        }

        return ProjectLikeResponse.builder()
                .liked(liked)
                .likesCount(projectLikeRepository.countByProjectId(projectId))
                .build();
    }

    // 16. Estado de "me gusta" para el usuario autenticado
    public ProjectLikeResponse getLikeStatus(Long projectId, String username) {
        String cleanUser = requireUsername(username);
        findProjectOrThrow(projectId);
        boolean liked = projectLikeRepository.findByProjectIdAndUsername(projectId, cleanUser).isPresent();
        return ProjectLikeResponse.builder()
                .liked(liked)
                .likesCount(projectLikeRepository.countByProjectId(projectId))
                .build();
    }

    private Project findProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
    }

    private String requireUsername(String username) {
        if (username == null || username.isBlank() || "anonimo".equalsIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return username;
    }

    private void assertOwnership(Project project, String username) {
        String owner = project.getCreatedBy();
        boolean isOwner = owner != null && (owner.equals(username)
                || (username.contains("@") && owner.equals(username.split("@")[0])));
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso sobre este proyecto");
        }
    }

    private void assertAccess(Project project, String username) {
        String owner = project.getCreatedBy();
        boolean isOwner = owner != null && (owner.equals(username)
                || (username.contains("@") && owner.equals(username.split("@")[0])));
        boolean isMember = isOwner || projectMemberRepository.existsByProjectIdAndUsername(project.getId(), username);
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso sobre este proyecto");
        }
    }
}
