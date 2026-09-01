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

import zentry.back.api.realtime.dtos.ConversationParticipantRequest;
import zentry.back.api.realtime.dtos.ConversationParticipantResponse;
import zentry.back.api.realtime.service.ConversationParticipantService;

@RestController
@RequestMapping("/api/realtime/conversation-participants")
@Tag(name = "Participantes de Conversación", description = "Gestión de participantes dentro de conversaciones — clave compuesta (conversationId + userId)")
public class ConversationParticipantController {

    private final ConversationParticipantService service;

    public ConversationParticipantController(ConversationParticipantService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar participantes de conversaciones",
               description = "Devuelve una lista paginada de todos los participantes registrados en conversaciones.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Page<ConversationParticipantResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{conversationId}/{userId}")
    @Operation(summary = "Obtener participante por clave compuesta",
               description = "Requiere conversationId y userId para identificar el registro de participación.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Participante encontrado"),
        @ApiResponse(responseCode = "400", description = "IDs inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Participante no encontrado", content = @Content)
    })
    public ResponseEntity<ConversationParticipantResponse> getById(
            @Parameter(description = "ID de la conversación") @PathVariable Integer conversationId,
            @Parameter(description = "ID del usuario participante") @PathVariable Integer userId) {
        return ResponseEntity.ok(service.getById(conversationId, userId));
    }

    @PostMapping
    @Operation(summary = "Agregar participante a conversación",
               description = "Registra un usuario como participante de una conversación. No puede existir ya la combinación conversationId + userId.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Participante agregado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Participante ya existente o datos inválidos", content = @Content)
    })
    public ResponseEntity<ConversationParticipantResponse> create(
            @Valid @RequestBody ConversationParticipantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @DeleteMapping("/{conversationId}/{userId}")
    @Operation(summary = "Eliminar participante de conversación",
               description = "Elimina la participación de un usuario en una conversación usando la clave compuesta.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Participante eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Participante no encontrado", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la conversación") @PathVariable Integer conversationId,
            @Parameter(description = "ID del usuario participante") @PathVariable Integer userId) {
        service.delete(conversationId, userId);
        return ResponseEntity.noContent().build();
    }
}
