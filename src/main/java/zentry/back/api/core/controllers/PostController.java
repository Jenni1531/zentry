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

import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.services.PostService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/core/posts", "/api/v1/posts"})
@Tag(name = "Posts", description = "Gestión de publicaciones y proyectos de estudio")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las publicaciones del Feed o filtrar por usuario (scope=my)")
    public ResponseEntity<?> getPosts(
            @RequestParam(required = false) String scope,
            @PageableDefault(size = 20) Pageable pageable,
            Principal principal) {
        if ("my".equalsIgnoreCase(scope) || "studio".equalsIgnoreCase(scope)) {
            if (principal == null) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(service.getMyPosts(principal.getName()));
        }
        return ResponseEntity.ok(service.getAllPosts(pageable));
    }

    @GetMapping("/my")
    @Operation(summary = "Obtener publicaciones y proyectos del estudio del usuario autenticado")
    public ResponseEntity<List<PostResponse>> getMyPosts(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(service.getMyPosts(principal.getName()));
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear publicación o proyecto de estudio (JSON)")
    public ResponseEntity<PostResponse> createJson(@Valid @RequestBody PostRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(username, request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear publicación con archivo multimedia (Multipart)")
    public ResponseEntity<PostResponse> createMultipart(@ModelAttribute PostRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(username, request));
    }

    @PostMapping
    @Operation(summary = "Crear publicación (Default)")
    public ResponseEntity<PostResponse> createDefault(@RequestBody(required = false) PostRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(username, request != null ? request : new PostRequest()));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Dar me gusta o reaccionar a una publicación")
    public ResponseEntity<PostResponse> likePost(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id) {
        return ResponseEntity.ok(service.likePost(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar publicación o proyecto (JSON)")
    public ResponseEntity<PostResponse> updateJson(
            @PathVariable Integer id,
            @Valid @RequestBody PostRequest request,
            Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        return ResponseEntity.ok(service.update(id, username, request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar publicación con archivo (Multipart)")
    public ResponseEntity<PostResponse> updateMultipart(
            @PathVariable Integer id,
            @ModelAttribute PostRequest request,
            Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        return ResponseEntity.ok(service.update(id, username, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar publicación (Default)")
    public ResponseEntity<PostResponse> updateDefault(
            @PathVariable Integer id,
            @RequestBody(required = false) PostRequest request,
            Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        return ResponseEntity.ok(service.update(id, username, request != null ? request : new PostRequest()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar publicación")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id,
            Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        service.delete(id, username);
        return ResponseEntity.noContent().build();
    }
}
