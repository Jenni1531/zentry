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

import zentry.back.api.core.dtos.ForumThreadRequest;
import zentry.back.api.core.dtos.ForumThreadResponse;
import zentry.back.api.core.services.ForumThreadService;

@RestController
@RequestMapping("/api/core/forum-threads")
@Tag(name = "Forum Threads", description = "Gestión de hilos de foro")
public class ForumThreadController {

    private final ForumThreadService service;

    public ForumThreadController(ForumThreadService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar hilos de foro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<Page<ForumThreadResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener hilo de foro por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Hilo encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    public ResponseEntity<ForumThreadResponse> getById(
            @Parameter(description = "ID del hilo") @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear hilo de foro")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<ForumThreadResponse> create(@Valid @RequestBody ForumThreadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar hilo de foro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    public ResponseEntity<ForumThreadResponse> update(
            @Parameter(description = "ID del hilo") @PathVariable Integer id,
            @Valid @RequestBody ForumThreadRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar hilo de foro")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del hilo") @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
