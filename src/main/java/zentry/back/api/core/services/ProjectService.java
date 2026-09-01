package zentry.back.api.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
<<<<<<< HEAD
import zentry.back.api.core.dtos.NoteRequestDTO;
import zentry.back.api.core.dtos.ProjectRequestDTO;
import zentry.back.api.core.dtos.ResourceRequestDTO;
=======
import zentry.back.api.core.dtos.ProjectRequestDTO;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
import zentry.back.api.core.dtos.TaskRequestDTO;
import zentry.back.api.core.models.*;
import zentry.back.api.core.repositories.ProjectRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    // 1. Obtener todos los proyectos del usuario
    public List<Project> getProjectsByUser(String username) {
<<<<<<< HEAD
        String cleanUser = requireUsername(username);
        List<Project> list = projectRepository.findByCreatedByOrderByUpdatedAtDesc(cleanUser);
        if (list.isEmpty() && cleanUser.contains("@")) {
            list = projectRepository.findByCreatedByOrderByUpdatedAtDesc(cleanUser.split("@")[0]);
        }
        return list;
    }

    // 2. Obtener un proyecto por ID (solo si pertenece al usuario autenticado)
    public Project getProjectById(Long id, String username) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(id);
        assertOwnership(project, cleanUser);
        return project;
=======
        return projectRepository.findByCreatedByOrderByUpdatedAtDesc(username);
    }

    // 2. Obtener un proyecto por ID
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    // 3. Crear nuevo proyecto
    @Transactional
    public Project createProject(String username, ProjectRequestDTO dto) {
<<<<<<< HEAD
        String cleanUser = requireUsername(username);

        Project project = Project.builder()
                .title(dto.getTitle() != null && !dto.getTitle().isBlank() ? dto.getTitle() : "Nuevo Proyecto")
                .description(dto.getDescription())
                .category(dto.getCategory() != null ? dto.getCategory() : "UI/UX")
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .status(dto.getStatus() != null ? dto.getStatus() : "active")
                .deadline(dto.getDeadline() != null ? dto.getDeadline() : "Sin fecha límite")
                .createdBy(cleanUser)
=======
        Project project = Project.builder()
                .title(dto.getTitle() != null ? dto.getTitle() : "Nuevo Proyecto")
                .description(dto.getDescription())
                .category(dto.getCategory() != null ? dto.getCategory() : "General")
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .status("active")
                .deadline(dto.getDeadline())
                .createdBy(username)
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
                .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : "")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

<<<<<<< HEAD
        String safeAvatar = cleanUser.length() >= 2 ? cleanUser.substring(0, 2).toUpperCase() : "ZN";

        // Agregar actividad inicial
        ProjectActivity initAct = ProjectActivity.builder()
                .user(cleanUser)
                .avatar(safeAvatar)
                .action("creó el proyecto")
                .target(project.getTitle())
=======
        String safeAvatar = (username != null && !username.isBlank())
                ? username.substring(0, Math.min(2, username.length())).toUpperCase()
                : "ZN";

        // Agregar actividad inicial
        ProjectActivity initAct = ProjectActivity.builder()
                .user(username)
                .avatar(safeAvatar)
                .action("creó el proyecto")
                .target(dto.getTitle() != null ? dto.getTitle() : "Proyecto")
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
                .iconType("member")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(initAct);
        return projectRepository.save(project);
    }

<<<<<<< HEAD
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
        projectRepository.delete(project);
    }

    // 6. Agregar Tarea
    @Transactional
    public ProjectTask addTask(Long projectId, String username, TaskRequestDTO dto) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertOwnership(project, cleanUser);

        ProjectTask task = ProjectTask.builder()
                .title(dto.getTitle() != null ? dto.getTitle() : "Nueva Tarea")
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .assignedTo(dto.getAssignedTo() != null ? dto.getAssignedTo() : cleanUser)
                .dueDate(dto.getDueDate() != null ? dto.getDueDate() : "Pronto")
=======
    // 4. Agregar Tarea
    @Transactional
    public ProjectTask addTask(Long projectId, String username, TaskRequestDTO dto) {
        Project project = getProjectById(projectId);

        ProjectTask task = ProjectTask.builder()
                .title(dto.getTitle())
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .assignedTo(dto.getAssignedTo() != null ? dto.getAssignedTo() : username)
                .dueDate(dto.getDueDate())
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
                .completed(false)
                .project(project)
                .build();

        project.getTasks().add(task);
        project.setUpdatedAt(LocalDateTime.now());

<<<<<<< HEAD
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
        assertOwnership(project, cleanUser);

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
=======
        String safeAvatar = (username != null && !username.isBlank())
                ? username.substring(0, Math.min(2, username.length())).toUpperCase()
                : "ZN";

        // Registrar actividad
        ProjectActivity act = ProjectActivity.builder()
                .user(username)
                .avatar(safeAvatar)
                .action("creó la tarea")
                .target(dto.getTitle())
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
                .iconType("task")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(act);
        projectRepository.save(project);
        return task;
    }

<<<<<<< HEAD
    // 8. Eliminar Tarea
    @Transactional
    public void deleteTask(Long projectId, Long taskId, String username) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertOwnership(project, cleanUser);

        project.getTasks().removeIf(t -> t.getId() != null && t.getId().equals(taskId));
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
    }

    // 9. Agregar Recurso
    @Transactional
    public ProjectResource addResource(Long projectId, String username, ResourceRequestDTO dto) {
        String cleanUser = requireUsername(username);
        Project project = findProjectOrThrow(projectId);
        assertOwnership(project, cleanUser);

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
        assertOwnership(project, cleanUser);

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
=======
    // 5. Toggle Completar Tarea
    @Transactional
    public ProjectTask toggleTask(Long projectId, Long taskId, String username) {
        Project project = getProjectById(projectId);
        ProjectTask task = project.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));

        task.setCompleted(!task.isCompleted());
        project.setUpdatedAt(LocalDateTime.now());

        if (task.isCompleted()) {
            String safeAvatar = (username != null && !username.isBlank())
                    ? username.substring(0, Math.min(2, username.length())).toUpperCase()
                    : "ZN";

            ProjectActivity act = ProjectActivity.builder()
                    .user(username)
                    .avatar(safeAvatar)
                    .action("completó la tarea")
                    .target(task.getTitle())
                    .iconType("task")
                    .timestamp(LocalDateTime.now())
                    .project(project)
                    .build();

            project.getActivities().add(act);
        }

        projectRepository.save(project);
        return task;
    }

    // 6. Búsqueda de proyectos
    public List<Project> searchProjects(String query) {
        return projectRepository.searchProjects(query);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }
}
