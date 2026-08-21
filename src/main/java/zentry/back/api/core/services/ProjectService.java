package zentry.back.api.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import zentry.back.api.core.dtos.ProjectRequestDTO;
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
        return projectRepository.findByCreatedByOrderByUpdatedAtDesc(username);
    }

    // 2. Obtener un proyecto por ID
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
    }

    // 3. Crear nuevo proyecto
    @Transactional
    public Project createProject(String username, ProjectRequestDTO dto) {
        Project project = Project.builder()
                .title(dto.getTitle() != null ? dto.getTitle() : "Nuevo Proyecto")
                .description(dto.getDescription())
                .category(dto.getCategory() != null ? dto.getCategory() : "General")
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .status("active")
                .deadline(dto.getDeadline())
                .createdBy(username)
                .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : "")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        String safeAvatar = (username != null && !username.isBlank())
                ? username.substring(0, Math.min(2, username.length())).toUpperCase()
                : "ZN";

        // Agregar actividad inicial
        ProjectActivity initAct = ProjectActivity.builder()
                .user(username)
                .avatar(safeAvatar)
                .action("creó el proyecto")
                .target(dto.getTitle() != null ? dto.getTitle() : "Proyecto")
                .iconType("member")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(initAct);
        return projectRepository.save(project);
    }

    // 4. Agregar Tarea
    @Transactional
    public ProjectTask addTask(Long projectId, String username, TaskRequestDTO dto) {
        Project project = getProjectById(projectId);

        ProjectTask task = ProjectTask.builder()
                .title(dto.getTitle())
                .priority(dto.getPriority() != null ? dto.getPriority() : "media")
                .assignedTo(dto.getAssignedTo() != null ? dto.getAssignedTo() : username)
                .dueDate(dto.getDueDate())
                .completed(false)
                .project(project)
                .build();

        project.getTasks().add(task);
        project.setUpdatedAt(LocalDateTime.now());

        String safeAvatar = (username != null && !username.isBlank())
                ? username.substring(0, Math.min(2, username.length())).toUpperCase()
                : "ZN";

        // Registrar actividad
        ProjectActivity act = ProjectActivity.builder()
                .user(username)
                .avatar(safeAvatar)
                .action("creó la tarea")
                .target(dto.getTitle())
                .iconType("task")
                .timestamp(LocalDateTime.now())
                .project(project)
                .build();

        project.getActivities().add(act);
        projectRepository.save(project);
        return task;
    }

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
    }
}
