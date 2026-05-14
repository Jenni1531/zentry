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

import zentry.back.api.core.dtos.CommentRequest;
import zentry.back.api.core.dtos.CommentResponse;
import zentry.back.api.core.services.CommentService;

@RestController
@RequestMapping("/api/core/comments")
@Tag(name = "Comments", description = "Gestión de comentarios en publicaciones")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @Operation(summary = "Listar comentarios", description = "Devuelve lista paginada de todos los comentarios.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @Operation(summary = "Obtener comentario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentario encontrado"),
        @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getById(
            @Parameter(description = "ID del comentario") @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Crear comentario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comentario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Actualizar comentario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comentario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> update(
            @Parameter(description = "ID del comentario") @PathVariable Integer id,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Eliminar comentario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comentario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Comentario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del comentario") @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
