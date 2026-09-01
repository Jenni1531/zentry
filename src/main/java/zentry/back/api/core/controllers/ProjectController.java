package zentry.back.api.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
import zentry.back.api.core.dtos.NoteRequestDTO;
import zentry.back.api.core.dtos.ProjectRequestDTO;
import zentry.back.api.core.dtos.ResourceRequestDTO;
import zentry.back.api.core.dtos.TaskRequestDTO;
import zentry.back.api.core.models.Project;
import zentry.back.api.core.models.ProjectNote;
import zentry.back.api.core.models.ProjectResource;
=======
import zentry.back.api.core.dtos.ProjectRequestDTO;
import zentry.back.api.core.dtos.TaskRequestDTO;
import zentry.back.api.core.models.Project;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
import zentry.back.api.core.models.ProjectTask;
import zentry.back.api.core.services.ProjectService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/core/projects", "/api/v1/projects"})
@RequiredArgsConstructor
<<<<<<< HEAD
@Tag(name = "Projects", description = "Gestión de proyectos, tareas, recursos y notas")
=======
@Tag(name = "Projects", description = "Gestión de proyectos, tareas y actividades")
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
public class ProjectController {

    private final ProjectService projectService;

    // GET /api/core/projects -> Lista de proyectos del usuario autenticado
    @GetMapping
    @Operation(summary = "Obtener proyectos del usuario autenticado")
    public ResponseEntity<List<Project>> getProjects(Principal principal) {
<<<<<<< HEAD
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(projectService.getProjectsByUser(username));
    }

    // GET /api/core/projects/{id} -> Detalle completo del proyecto (solo si es del usuario autenticado)
    @GetMapping("/{id}")
    @Operation(summary = "Detalle completo del proyecto por ID")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(projectService.getProjectById(id, username));
=======
        String username = principal != null ? principal.getName() : "anonimo";
        return ResponseEntity.ok(projectService.getProjectsByUser(username));
    }

    // GET /api/core/projects/{id} -> Detalle completo del proyecto
    @GetMapping("/{id}")
    @Operation(summary = "Detalle completo del proyecto por ID")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    // POST /api/core/projects -> Crear nuevo proyecto
    @PostMapping
    @Operation(summary = "Crear nuevo proyecto")
    public ResponseEntity<Project> createProject(
            @RequestBody ProjectRequestDTO dto,
            Principal principal) {
<<<<<<< HEAD
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(projectService.createProject(username, dto));
    }

    // PUT /api/core/projects/{id} -> Actualizar proyecto
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proyecto existente")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectRequestDTO dto,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(projectService.updateProject(id, username, dto));
    }

    // DELETE /api/core/projects/{id} -> Eliminar proyecto
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proyecto")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        projectService.deleteProject(id, username);
        return ResponseEntity.noContent().build();
    }

=======
        String username = principal != null ? principal.getName() : "creador";
        return ResponseEntity.ok(projectService.createProject(username, dto));
    }

>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    // POST /api/core/projects/{id}/tasks -> Añadir tarea
    @PostMapping("/{id}/tasks")
    @Operation(summary = "Añadir tarea a un proyecto")
    public ResponseEntity<ProjectTask> addTask(
            @PathVariable Long id,
            @RequestBody TaskRequestDTO dto,
            Principal principal) {
<<<<<<< HEAD
        String username = principal != null ? principal.getName() : null;
=======
        String username = principal != null ? principal.getName() : "creador";
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
        return ResponseEntity.ok(projectService.addTask(id, username, dto));
    }

    // PUT /api/core/projects/{id}/tasks/{taskId}/toggle -> Marcar / desmarcar tarea
    @PutMapping("/{id}/tasks/{taskId}/toggle")
    @Operation(summary = "Alternar estado completado de una tarea")
    public ResponseEntity<ProjectTask> toggleTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            Principal principal) {
<<<<<<< HEAD
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(projectService.toggleTask(id, taskId, username));
    }

    // DELETE /api/core/projects/{id}/tasks/{taskId} -> Eliminar tarea
    @DeleteMapping("/{id}/tasks/{taskId}")
    @Operation(summary = "Eliminar tarea de un proyecto")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        projectService.deleteTask(id, taskId, username);
        return ResponseEntity.noContent().build();
    }

    // POST /api/core/projects/{id}/resources -> Añadir recurso
    @PostMapping("/{id}/resources")
    @Operation(summary = "Añadir recurso o archivo a un proyecto")
    public ResponseEntity<ProjectResource> addResource(
            @PathVariable Long id,
            @RequestBody ResourceRequestDTO dto,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(projectService.addResource(id, username, dto));
    }

    // POST /api/core/projects/{id}/notes -> Añadir nota
    @PostMapping("/{id}/notes")
    @Operation(summary = "Añadir nota o documentación a un proyecto")
    public ResponseEntity<ProjectNote> addNote(
            @PathVariable Long id,
            @RequestBody NoteRequestDTO dto,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(projectService.addNote(id, username, dto));
    }

    // GET /api/core/projects/search -> Buscar proyectos (acotado al usuario autenticado)
    @GetMapping("/search")
    @Operation(summary = "Buscar proyectos por término")
    public ResponseEntity<List<Project>> searchProjects(@RequestParam("q") String query, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(projectService.searchProjects(username, query));
=======
        String username = principal != null ? principal.getName() : "creador";
        return ResponseEntity.ok(projectService.toggleTask(id, taskId, username));
    }

    // GET /api/core/projects/search -> Buscar proyectos
    @GetMapping("/search")
    @Operation(summary = "Buscar proyectos por término")
    public ResponseEntity<List<Project>> searchProjects(@RequestParam("q") String query) {
        return ResponseEntity.ok(projectService.searchProjects(query));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }
}
