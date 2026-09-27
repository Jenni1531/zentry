package zentry.back.api.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zentry.back.api.core.dtos.*;
import zentry.back.api.core.models.ProjectNote;
import zentry.back.api.core.models.ProjectResource;
import zentry.back.api.core.models.ProjectTask;
import zentry.back.api.core.services.ProjectService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/core/projects", "/api/v1/projects"})
@Tag(name = "Projects", description = "Proyectos colaborativos: drive, tareas, libro, obra compartida, chat y publicación")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    private static String who(Principal principal) {
        return principal != null ? principal.getName() : null;
    }

    // ---------- Proyectos ----------

    @GetMapping
    @Operation(summary = "Mis proyectos (propios y donde colaboro)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista obtenida"),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content) })
    public ResponseEntity<List<ProjectResponse>> getProjects(Principal principal) {
        return ResponseEntity.ok(projectService.getProjectsByUser(who(principal)));
    }

    @GetMapping("/public")
    @Operation(summary = "Proyectos públicos (Explorar)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Página obtenida") })
    public ResponseEntity<Page<ProjectResponse>> getPublicProjects(@PageableDefault(size = 20) Pageable pageable, Principal principal) {
        return ResponseEntity.ok(projectService.getPublicProjects(who(principal), pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar entre mis proyectos y los públicos")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Resultados") })
    public ResponseEntity<List<ProjectResponse>> searchProjects(@RequestParam("q") String query, Principal principal) {
        return ResponseEntity.ok(projectService.searchProjects(who(principal), query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle del proyecto", description = "Público: cualquiera (solo lectura). Privado: solo miembros.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Detalle obtenido"),
                    @ApiResponse(responseCode = "403", description = "Proyecto privado", content = @Content),
                    @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content) })
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(projectService.getProjectById(id, who(principal)));
    }

    @PostMapping
    @Operation(summary = "Crear proyecto", description = "projectType: general | book | image | video | audio · visibility: private | public")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Proyecto creado") })
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequestDTO dto, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(who(principal), dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proyecto (solo dueño)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Actualizado"),
                    @ApiResponse(responseCode = "403", description = "No es el dueño", content = @Content) })
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(projectService.updateProject(id, who(principal), dto));
    }

    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir portada del proyecto (solo dueño)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Portada actualizada"),
                    @ApiResponse(responseCode = "400", description = "Archivo no válido", content = @Content) })
    public ResponseEntity<ProjectResponse> uploadCover(@PathVariable Long id, @RequestPart("file") MultipartFile file, Principal principal) {
        return ResponseEntity.ok(projectService.uploadCover(id, who(principal), file));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proyecto (solo dueño)")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Eliminado"),
                    @ApiResponse(responseCode = "403", description = "No es el dueño", content = @Content) })
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Principal principal) {
        projectService.deleteProject(id, who(principal));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publicar el proyecto terminado en el feed principal (solo dueño)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Publicado"),
                    @ApiResponse(responseCode = "400", description = "Falta contenido o ya estaba publicado", content = @Content) })
    public ResponseEntity<ProjectResponse> publish(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(projectService.publishToFeed(id, who(principal)));
    }

    // ---------- Libro (capítulos) ----------

    @GetMapping("/{id}/chapters")
    @Operation(summary = "Capítulos del libro")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista de capítulos") })
    public ResponseEntity<List<ProjectChapterResponse>> listChapters(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(projectService.listChapters(id, who(principal)));
    }

    @PostMapping("/{id}/chapters")
    @Operation(summary = "Añadir capítulo (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "201", description = "Capítulo creado") })
    public ResponseEntity<ProjectChapterResponse> addChapter(@PathVariable Long id, @Valid @RequestBody ProjectChapterRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.addChapter(id, who(principal), request));
    }

    @PutMapping("/{id}/chapters/{chapterId}")
    @Operation(summary = "Editar o reordenar capítulo (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Capítulo actualizado"),
                    @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content) })
    public ResponseEntity<ProjectChapterResponse> updateChapter(@PathVariable Long id, @PathVariable Long chapterId,
                                                                @Valid @RequestBody ProjectChapterRequest request, Principal principal) {
        return ResponseEntity.ok(projectService.updateChapter(id, chapterId, who(principal), request));
    }

    @DeleteMapping("/{id}/chapters/{chapterId}")
    @Operation(summary = "Eliminar capítulo (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Eliminado") })
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id, @PathVariable Long chapterId, Principal principal) {
        projectService.deleteChapter(id, chapterId, who(principal));
        return ResponseEntity.noContent().build();
    }

    // ---------- Tareas y notas ----------

    @PostMapping("/{id}/tasks")
    @Operation(summary = "Añadir tarea (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Tarea creada") })
    public ResponseEntity<ProjectTask> addTask(@PathVariable Long id, @RequestBody TaskRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(projectService.addTask(id, who(principal), dto));
    }

    @PutMapping("/{id}/tasks/{taskId}/toggle")
    @Operation(summary = "Marcar / desmarcar tarea (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Tarea actualizada") })
    public ResponseEntity<ProjectTask> toggleTask(@PathVariable Long id, @PathVariable Long taskId, Principal principal) {
        return ResponseEntity.ok(projectService.toggleTask(id, taskId, who(principal)));
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    @Operation(summary = "Eliminar tarea (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Eliminada") })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @PathVariable Long taskId, Principal principal) {
        projectService.deleteTask(id, taskId, who(principal));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/notes")
    @Operation(summary = "Añadir nota (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Nota creada") })
    public ResponseEntity<ProjectNote> addNote(@PathVariable Long id, @RequestBody NoteRequestDTO dto, Principal principal) {
        return ResponseEntity.ok(projectService.addNote(id, who(principal), dto));
    }

    // ---------- Drive ----------

    @PostMapping(value = "/{id}/resources", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Subir archivos, crear carpeta o añadir enlace al drive (miembros)",
               description = "Multipart: files[] (uno o varios) + folder. JSON/partes: name, type=FOLDER para crear carpeta, url para enlaces.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Recursos creados") })
    public ResponseEntity<List<ProjectResource>> addResource(
            @PathVariable Long id,
            @RequestPart(value = "data", required = false) ResourceRequestDTO jsonDto,
            @RequestParam(value = "name", required = false) String nameParam,
            @RequestParam(value = "type", required = false) String typeParam,
            @RequestParam(value = "url", required = false) String urlParam,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Principal principal) {
        ResourceRequestDTO dto = jsonDto != null ? jsonDto : new ResourceRequestDTO();
        if (jsonDto == null) {
            dto.setName(nameParam);
            dto.setType(typeParam);
            dto.setUrl(urlParam);
        }
        List<MultipartFile> all = new ArrayList<>();
        if (file != null && !file.isEmpty()) all.add(file);
        if (files != null) files.stream().filter(f -> f != null && !f.isEmpty()).forEach(all::add);

        List<ProjectResource> created = new ArrayList<>();
        if (all.isEmpty()) {
            created.add(projectService.addResource(id, who(principal), dto, null, folder));
        } else {
            for (MultipartFile f : all) {
                ResourceRequestDTO single = new ResourceRequestDTO();
                single.setName(all.size() == 1 ? dto.getName() : null);
                created.add(projectService.addResource(id, who(principal), single, f, folder));
            }
        }
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/resources/{resourceId}")
    @Operation(summary = "Renombrar o mover un archivo/carpeta (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Actualizado") })
    public ResponseEntity<ProjectResource> updateResource(@PathVariable Long id, @PathVariable Long resourceId,
                                                          @Valid @RequestBody ProjectResourceUpdateRequest request, Principal principal) {
        return ResponseEntity.ok(projectService.updateResource(id, resourceId, who(principal), request));
    }

    @DeleteMapping("/{id}/resources/{resourceId}")
    @Operation(summary = "Eliminar archivo o carpeta con su contenido (miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Eliminado") })
    public ResponseEntity<Void> deleteResource(@PathVariable Long id, @PathVariable Long resourceId, Principal principal) {
        projectService.deleteResource(id, resourceId, who(principal));
        return ResponseEntity.noContent().build();
    }

    // ---------- Miembros, chat y likes ----------

    @GetMapping("/{id}/members")
    @Operation(summary = "Miembros del proyecto")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista de miembros") })
    public ResponseEntity<List<ProjectMemberResponse>> listMembers(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(projectService.listMembers(id, who(principal)));
    }

    @PostMapping("/{id}/invite")
    @Operation(summary = "Agregar colaborador por @usuario o email (solo dueño)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Colaborador agregado"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content) })
    public ResponseEntity<ProjectMemberResponse> inviteMember(@PathVariable Long id, @Valid @RequestBody InviteMemberRequest request, Principal principal) {
        return ResponseEntity.ok(projectService.inviteMember(id, who(principal), request));
    }

    @DeleteMapping("/{id}/members/{username}")
    @Operation(summary = "Quitar colaborador o salir del proyecto")
    @ApiResponses({ @ApiResponse(responseCode = "204", description = "Quitado") })
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable String username, Principal principal) {
        projectService.removeMember(id, who(principal), username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/chat")
    @Operation(summary = "Id del chat de grupo del proyecto (solo miembros)")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Chat disponible"),
                    @ApiResponse(responseCode = "403", description = "No es miembro", content = @Content) })
    public ResponseEntity<Map<String, Object>> getChat(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(projectService.getChat(id, who(principal)));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Alternar me gusta en un proyecto")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Estado actualizado") })
    public ResponseEntity<ProjectLikeResponse> toggleLike(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(projectService.toggleLike(id, who(principal)));
    }

    @GetMapping("/{id}/like")
    @Operation(summary = "Consultar estado de me gusta")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Estado obtenido") })
    public ResponseEntity<ProjectLikeResponse> getLikeStatus(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(projectService.getLikeStatus(id, who(principal)));
    }
}
