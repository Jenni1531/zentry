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
<<<<<<< HEAD
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
=======
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.services.PostService;
<<<<<<< HEAD

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/core/posts", "/api/v1/posts"})
@Tag(name = "Posts", description = "Gestión de publicaciones y proyectos de estudio")
=======
import org.springframework.http.MediaType;
import java.security.Principal;

@RestController
@RequestMapping({"/api/core/posts", "/api/v1/posts"})
@Tag(name = "Posts", description = "Gestión de publicaciones")
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

<<<<<<< HEAD
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
=======
    /*
    @GetMapping
    @Operation(summary = "Listar publicaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
    })
    public ResponseEntity<Page<PostResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }
    */
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

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

<<<<<<< HEAD
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
=======
    @GetMapping
    @Operation(summary = "Obtener todas las publicaciones del Feed")
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(service.getAllPosts(pageable));
    }

    @Operation(summary = "Crear publicación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)    
    public ResponseEntity<PostResponse> create(@ModelAttribute PostRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(principal.getName(), request));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Dar me gusta o reaccionar a una publicación")
    public ResponseEntity<PostResponse> likePost(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id) {
        return ResponseEntity.ok(service.likePost(id));
    }

<<<<<<< HEAD
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
=======
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar publicación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    public ResponseEntity<PostResponse> update(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id,
            @Valid @RequestBody PostRequest request, Principal principal) {
        return ResponseEntity.ok(service.update(id, principal.getName(),request));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar publicación")
<<<<<<< HEAD
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id,
            Principal principal) {
        String username = principal != null ? principal.getName() : "anonimo";
        service.delete(id, username);
        return ResponseEntity.noContent().build();
    }
=======
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "No encontrada", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la publicación") @PathVariable Integer id, Principal principal) {
        service.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
}
