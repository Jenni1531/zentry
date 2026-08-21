package zentry.back.api.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zentry.back.api.core.dtos.ProjectRequestDTO;
import zentry.back.api.core.dtos.TaskRequestDTO;
import zentry.back.api.core.models.Project;
import zentry.back.api.core.models.ProjectTask;
import zentry.back.api.core.services.ProjectService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/core/projects", "/api/v1/projects"})
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Gestión de proyectos, tareas y actividades")
public class ProjectController {

    private final ProjectService projectService;

    // GET /api/core/projects -> Lista de proyectos del usuario autenticado
    @GetMapping
    @Operation(summary = "Obtener proyectos del usuario autenticado")
    public ResponseEntity<List<Project>> getProjects(Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        return ResponseEntity.ok(projectService.getProjectsByUser(username));
    }

    // GET /api/core/projects/{id} -> Detalle completo del proyecto
    @GetMapping("/{id}")
    @Operation(summary = "Detalle completo del proyecto por ID")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    // POST /api/core/projects -> Crear nuevo proyecto
    @PostMapping
    @Operation(summary = "Crear nuevo proyecto")
    public ResponseEntity<Project> createProject(
            @RequestBody ProjectRequestDTO dto,
            Principal principal) {
        String username = principal != null ? principal.getName() : "creador";
        return ResponseEntity.ok(projectService.createProject(username, dto));
    }

    // POST /api/core/projects/{id}/tasks -> Añadir tarea
    @PostMapping("/{id}/tasks")
    @Operation(summary = "Añadir tarea a un proyecto")
    public ResponseEntity<ProjectTask> addTask(
            @PathVariable Long id,
            @RequestBody TaskRequestDTO dto,
            Principal principal) {
        String username = principal != null ? principal.getName() : "creador";
        return ResponseEntity.ok(projectService.addTask(id, username, dto));
    }

    // PUT /api/core/projects/{id}/tasks/{taskId}/toggle -> Marcar / desmarcar tarea
    @PutMapping("/{id}/tasks/{taskId}/toggle")
    @Operation(summary = "Alternar estado completado de una tarea")
    public ResponseEntity<ProjectTask> toggleTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            Principal principal) {
        String username = principal != null ? principal.getName() : "creador";
        return ResponseEntity.ok(projectService.toggleTask(id, taskId, username));
    }

    // GET /api/core/projects/search -> Buscar proyectos
    @GetMapping("/search")
    @Operation(summary = "Buscar proyectos por término")
    public ResponseEntity<List<Project>> searchProjects(@RequestParam("q") String query) {
        return ResponseEntity.ok(projectService.searchProjects(query));
    }
}
