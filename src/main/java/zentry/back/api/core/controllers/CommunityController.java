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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import zentry.back.api.core.dtos.CommunityRequest;
import zentry.back.api.core.dtos.CommunityResponse;
import zentry.back.api.core.services.CommunityService;

@RestController
@RequestMapping("/api/core/communities")
@Tag(name = "Communities", description = "Gestión de comunidades de usuarios")
public class CommunityController {

    private final CommunityService service;

    public CommunityController(CommunityService service) {
        this.service = service;
    }

    @Operation(summary = "Listar comunidades", description = "Devuelve lista paginada de todas las comunidades.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<CommunityResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @Operation(summary = "Obtener comunidad por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comunidad encontrada"),
        @ApiResponse(responseCode = "404", description = "Comunidad no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponse> getById(
            @Parameter(description = "ID de la comunidad") @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
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

    @Operation(summary = "Actualizar comunidad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comunidad actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comunidad no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommunityResponse> update(
            @Parameter(description = "ID de la comunidad") @PathVariable Integer id,
            @Valid @RequestBody CommunityRequest request, Principal principal) {
        return ResponseEntity.ok(service.update(id,principal.getName(),  request));
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
