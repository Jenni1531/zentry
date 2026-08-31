package zentry.back.api.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import zentry.back.api.core.dtos.NoteRequestDTO;
import zentry.back.api.core.dtos.ProjectRequestDTO;
import zentry.back.api.core.dtos.ResourceRequestDTO;
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
        String cleanUser = (username != null && !username.isBlank() && !"anonimo".equalsIgnoreCase(username))
                ? username : "creador";
        List<Project> list = projectRepository.findByCreatedByOrderByUpdatedAtDesc(cleanUser);
        if (list.isEmpty() && cleanUser.contains("@")) {
            list = projectRepository.findByCreatedByOrderByUpdatedAtDesc(cleanUser.split("@")[0]);
        }
        return list;
    }

    // 2. Obtener un proyecto por ID
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
    }

    // 3. Crear nuevo proyecto
    @Transactional
    public Project createProject(String username, ProjectRequestDTO dto) {
        String cleanUser = (username != null && !username.isBlank() && !"anonimo".equalsIgnoreCase(username))
                ? username : "creador";

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
        return projectRepository.save(project);
    }

    // 4. Actualizar Proyecto
    @Transactional
    public Project updateProject(Long id, String username, ProjectRequestDTO dto) {
        Project project = getProjectById(id);

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) project.setTitle(dto.getTitle());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getCategory() != null) project.setCategory(dto.getCategory());
        if (dto.getPriority() != null) project.setPriority(dto.getPriority());
        if (dto.getStatus() != null) project.setStatus(dto.getStatus());
        if (dto.getDeadline() != null) project.setDeadline(dto.getDeadline());
        if (dto.getTags() != null) project.setTags(String.join(",", dto.getTags()));

        project.setUpdatedAt(LocalDateTime.now());

        String cleanUser = (username != null && !username.isBlank()) ? username : "creador";
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
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }

    // 6. Agregar Tarea
    @Transactional
    public ProjectTask addTask(Long projectId, String username, TaskRequestDTO dto) {
        Project project = getProjectById(projectId);
        String cleanUser = (username != null && !username.isBlank()) ? username : "creador";

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
        Project project = getProjectById(projectId);
        ProjectTask task = project.getTasks().stream()
                .filter(t -> t.getId() != null && t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));

        task.setCompleted(!task.isCompleted());
        project.setUpdatedAt(LocalDateTime.now());

        String cleanUser = (username != null && !username.isBlank()) ? username : "creador";
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
        Project project = getProjectById(projectId);
        project.getTasks().removeIf(t -> t.getId() != null && t.getId().equals(taskId));
        project.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(project);
    }

    // 9. Agregar Recurso
    @Transactional
    public ProjectResource addResource(Long projectId, String username, ResourceRequestDTO dto) {
        Project project = getProjectById(projectId);
        String cleanUser = (username != null && !username.isBlank()) ? username : "creador";

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
        Project project = getProjectById(projectId);
        String cleanUser = (username != null && !username.isBlank()) ? username : "creador";

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

    // 11. Búsqueda de proyectos
    public List<Project> searchProjects(String query) {
        return projectRepository.searchProjects(query);
    }
}
