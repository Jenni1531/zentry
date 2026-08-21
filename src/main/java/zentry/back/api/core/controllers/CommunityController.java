package zentry.back.api.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.security.Principal;
import java.util.Map;
import zentry.back.api.core.dtos.CommunityRequest;
import zentry.back.api.core.dtos.CommunityResponse;
import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.services.CommunityService;

@RestController
@RequestMapping("/api/core/communities")
@Tag(name = "Communities", description = "Gestión de comunidades de usuarios")
public class CommunityController {

    private final CommunityService service;

    public CommunityController(CommunityService service) {
        this.service = service;
    }

    @Operation(summary = "Listar y buscar comunidades", description = "Devuelve lista paginada de comunidades con filtro de búsqueda opcional.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<CommunityResponse>> list(
            @RequestParam(value = "search", required = false) String search,
            @PageableDefault(size = 20) Pageable pageable,
            Principal principal) {
        String currentUserEmail = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(service.list(search, pageable, currentUserEmail));
    }

    @Operation(summary = "Obtener detalle de comunidad por ID o Slug")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comunidad encontrada"),
        @ApiResponse(responseCode = "404", description = "Comunidad no encontrada", content = @Content)
    })
    @GetMapping("/{identifier}")
    public ResponseEntity<CommunityResponse> getByIdentifier(
            @Parameter(description = "ID o Slug de la comunidad") @PathVariable String identifier,
            Principal principal) {
        String currentUserEmail = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(service.getByIdentifier(identifier, currentUserEmail));
    }

    @Operation(summary = "Crear comunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comunidad creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CommunityResponse> create(
            @Valid @RequestBody CommunityRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(principal.getName(), request));
    }

    @Operation(summary = "Actualizar configuración de la comunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comunidad actualizada exitosamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado / No es el creador", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comunidad no encontrada", content = @Content)
    })
    @PutMapping(value = "/{identifier}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<CommunityResponse> updateConfig(
            @Parameter(description = "ID o Slug de la comunidad") @PathVariable String identifier,
            @RequestPart(value = "data", required = false) @Valid CommunityRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @RequestPart(value = "banner", required = false) MultipartFile banner,
            Principal principal) {
        if (request == null) {
            request = new CommunityRequest();
        }
        return ResponseEntity.ok(service.updateConfig(identifier, principal.getName(), request, avatar, banner));
    }

    @Operation(summary = "Unirse a la comunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unido exitosamente a la comunidad"),
        @ApiResponse(responseCode = "404", description = "Comunidad no encontrada", content = @Content)
    })
    @PostMapping("/{identifier}/join")
    public ResponseEntity<CommunityResponse> join(
            @Parameter(description = "ID o Slug de la comunidad") @PathVariable String identifier,
            Principal principal) {
        return ResponseEntity.ok(service.joinCommunity(identifier, principal.getName()));
    }

    @Operation(summary = "Salir de la comunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Salida exitosa de la comunidad"),
        @ApiResponse(responseCode = "404", description = "Comunidad no encontrada", content = @Content)
    })
    @PostMapping("/{identifier}/leave")
    public ResponseEntity<CommunityResponse> leave(
            @Parameter(description = "ID o Slug de la comunidad") @PathVariable String identifier,
            Principal principal) {
        return ResponseEntity.ok(service.leaveCommunity(identifier, principal.getName()));
    }

    @Operation(summary = "Crear publicación en la comunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Publicación creada en la comunidad"),
        @ApiResponse(responseCode = "404", description = "Comunidad o usuario no encontrado", content = @Content)
    })
    @PostMapping(value = "/{identifier}/posts", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PostResponse> createPostInCommunity(
            @Parameter(description = "ID o Slug de la comunidad") @PathVariable String identifier,
            @Valid @ModelAttribute PostRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPostInCommunity(identifier, principal.getName(), request));
    }

    @Operation(summary = "Activar o desactivar notificaciones de la comunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Preferencia de notificaciones actualizada"),
        @ApiResponse(responseCode = "404", description = "Comunidad no encontrada", content = @Content)
    })
    @PostMapping("/{identifier}/notifications/toggle")
    public ResponseEntity<Map<String, Object>> toggleNotifications(
            @Parameter(description = "ID o Slug de la comunidad") @PathVariable String identifier,
            Principal principal) {
        return ResponseEntity.ok(service.toggleNotifications(identifier, principal.getName()));
    }

    @Operation(summary = "Eliminar comunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comunidad eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Comunidad no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la comunidad") @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
