package zentry.back.api.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.util.List;
import zentry.back.api.core.dtos.ProfileRequest;
import zentry.back.api.core.dtos.ProfileResponse;
import zentry.back.api.core.services.ProfileService;

@RestController
@RequestMapping({"/api/core/profiles", "/api/v1/profiles"})
@Tag(name = "Profiles", description = "Gestión de perfiles de usuario")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar perfiles por nombre")
    public ResponseEntity<List<ProfileResponse>> searchProfiles(@RequestParam("q") String query) {
        return ResponseEntity.ok(service.searchProfiles(query));
    }

    @GetMapping("/{identifier}")
    @Operation(summary = "Obtener perfil por identificador (username o email)",
               description = "Lectura pública de perfil. Retorna la información del creador y booleano isFollowing si el cliente está autenticado.")
    public ResponseEntity<ProfileResponse> getProfile(
            @PathVariable String identifier,
            Principal principal) {
        String currentUsername = principal != null ? principal.getName() : null;
        ProfileResponse profile = service.getProfileByUsername(identifier, currentUsername);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PutMapping(value = "/me", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "Actualizar mi propio perfil (Solo el usuario autenticado)")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @ModelAttribute ProfileRequest request,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(service.updateMyProfile(principal.getName(), request));
    }

    @PostMapping("/{identifier}/follow")
    @Operation(summary = "Seguir o dejar de seguir a otro creador",
               description = "Acción social que requiere autenticación. Alterna el estado de seguimiento.")
    public ResponseEntity<java.util.Map<String, Object>> toggleFollow(
            @PathVariable String identifier,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String currentUsername = principal.getName();
        if (currentUsername.equalsIgnoreCase(identifier)) {
            return ResponseEntity.badRequest().build();
        }

        boolean isFollowing = service.toggleFollow(currentUsername, identifier);
        return ResponseEntity.ok(java.util.Map.of("isFollowing", isFollowing));
    }
}
