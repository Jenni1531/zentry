package zentry.back.api.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zentry.back.api.core.dtos.StudioProjectRequest;
import zentry.back.api.core.dtos.StudioProjectResponse;
import zentry.back.api.core.models.ContentType;
import zentry.back.api.core.services.StudioProjectService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/core/projects", "/api/v1/projects"})
@Tag(name = "Projects", description = "Gestión de proyectos del estudio creativo")
public class ProjectController {

    private final StudioProjectService service;

    public ProjectController(StudioProjectService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener proyectos del usuario autenticado")
    public ResponseEntity<List<StudioProjectResponse>> getUserProjects(
            @RequestParam(value = "type", required = false) String typeStr,
            Principal principal) {
        ContentType type = null;
        if (typeStr != null && !typeStr.isBlank()) {
            try {
                type = ContentType.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        String username = principal != null ? principal.getName() : "admin@zentry.com";
        return ResponseEntity.ok(service.getUserProjects(username, type));
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Crear nuevo proyecto")
    public ResponseEntity<StudioProjectResponse> createProject(
            @RequestPart(value = "data", required = false) @Valid StudioProjectRequest jsonRequest,
            @RequestParam(value = "title", required = false) String titleParam,
            @RequestParam(value = "description", required = false) String descriptionParam,
            @RequestParam(value = "type", required = false) String typeParam,
            @RequestParam(value = "tools", required = false) List<String> toolsParam,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) {

        StudioProjectRequest request = jsonRequest;
        if (request == null) {
            ContentType contentType = ContentType.CANVAS;
            if (typeParam != null && !typeParam.isBlank()) {
                try {
                    contentType = ContentType.valueOf(typeParam.toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }

            request = StudioProjectRequest.builder()
                    .title(titleParam != null ? titleParam : "Proyecto sin título")
                    .description(descriptionParam)
                    .type(contentType)
                    .tools(toolsParam)
                    .build();
        }

        String username = principal != null ? principal.getName() : "admin@zentry.com";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createProject(username, request, file));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proyecto por ID")
    public ResponseEntity<StudioProjectResponse> getProjectDetail(
            @PathVariable Integer id,
            Principal principal) {
        String username = principal != null ? principal.getName() : "admin@zentry.com";
        return ResponseEntity.ok(service.getProjectDetail(id, username));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proyecto por ID")
    public ResponseEntity<StudioProjectResponse> updateProject(
            @PathVariable Integer id,
            @Valid @RequestBody StudioProjectRequest request,
            Principal principal) {
        String username = principal != null ? principal.getName() : "admin@zentry.com";
        return ResponseEntity.ok(service.updateProject(id, username, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proyecto por ID")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Integer id,
            Principal principal) {
        String username = principal != null ? principal.getName() : "admin@zentry.com";
        service.deleteProject(id, username);
        return ResponseEntity.noContent().build();
    }
}
