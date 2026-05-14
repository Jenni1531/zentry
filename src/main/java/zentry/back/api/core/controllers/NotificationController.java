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

import zentry.back.api.core.dtos.NotificationRequest;
import zentry.back.api.core.dtos.NotificationResponse;
import zentry.back.api.core.services.NotificationService;

@RestController
@RequestMapping("/api/core/notifications")
@Tag(name = "Notifications", description = "Gestión de notificaciones de usuarios")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar notificaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<Page<NotificationResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener notificación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    public ResponseEntity<NotificationResponse> getById(
            @Parameter(description = "ID de la notificación") @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear notificación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar notificación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    public ResponseEntity<NotificationResponse> update(
            @Parameter(description = "ID de la notificación") @PathVariable Integer id,
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar notificación")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la notificación") @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
