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

import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.services.PostService;

@RestController
@RequestMapping("/api/core/posts")
@Tag(name = "Posts", description = "Gestión de publicaciones")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar publicaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<Page<PostResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener publicación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Publicación encontrada"),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    public ResponseEntity<PostResponse> getById(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear publicación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar publicación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    public ResponseEntity<PostResponse> update(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar publicación")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
