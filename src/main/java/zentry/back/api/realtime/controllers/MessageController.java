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

import zentry.back.api.realtime.dtos.MessageRequest;
import zentry.back.api.realtime.dtos.MessageResponse;
import zentry.back.api.realtime.service.MessageService;

@RestController
@RequestMapping("/api/realtime/messages")
@Tag(name = "Mensajes", description = "Gestión de mensajes enviados dentro de conversaciones en tiempo real")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar mensajes",
               description = "Devuelve una lista paginada de todos los mensajes registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<Page<MessageResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mensaje por ID",
               description = "Devuelve los datos de un mensaje específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje encontrado"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado", content = @Content)
    })
    public ResponseEntity<MessageResponse> getById(
            @Parameter(description = "ID del mensaje") @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Enviar mensaje",
               description = "Envía un nuevo mensaje dentro de una conversación en tiempo real.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mensaje enviado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content)
    })
    public ResponseEntity<MessageResponse> create(
            @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar mensaje",
               description = "Modifica el contenido de un mensaje existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado", content = @Content)
    })
    public ResponseEntity<MessageResponse> update(
            @Parameter(description = "ID del mensaje") @PathVariable Integer id,
            @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mensaje",
               description = "Elimina permanentemente un mensaje por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Mensaje eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del mensaje") @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
