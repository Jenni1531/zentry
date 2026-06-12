package zentry.back.api.realtime.controllers;

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

import zentry.back.api.realtime.dtos.ConversationRequest;
import zentry.back.api.realtime.dtos.ConversationResponse;
import zentry.back.api.realtime.service.ConversationService;

@RestController
@RequestMapping("/api/realtime/conversations")
@Tag(name = "Conversaciones", description = "Gestión de conversaciones en tiempo real entre usuarios")
public class ConversationController {

    private final ConversationService service;

    public ConversationController(ConversationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar conversaciones",
               description = "Devuelve una lista paginada de todas las conversaciones registradas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Page<ConversationResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener conversación por ID",
               description = "Devuelve los datos de una conversación específica.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conversación encontrada"),
        @ApiResponse(responseCode = "404", description = "Conversación no encontrada", content = @Content)
    })
    public ResponseEntity<ConversationResponse> getById(
            @Parameter(description = "ID de la conversación") @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear conversación",
               description = "Crea una nueva conversación en tiempo real.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Conversación creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content)
    })
    public ResponseEntity<ConversationResponse> create(
            @Valid @RequestBody ConversationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar conversación",
               description = "Modifica los datos de una conversación existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conversación actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
        @ApiResponse(responseCode = "404", description = "Conversación no encontrada", content = @Content)
    })
    public ResponseEntity<ConversationResponse> update(
            @Parameter(description = "ID de la conversación") @PathVariable Integer id,
            @Valid @RequestBody ConversationRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar conversación",
               description = "Elimina permanentemente una conversación por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Conversación eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Conversación no encontrada", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la conversación") @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
