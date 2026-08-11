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
@RequestMapping("/api/core/profiles")
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

    @GetMapping("/{username}")
    @Operation(summary = "Obtener perfil por nombre de usuario público")
    public ResponseEntity<ProfileResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(service.getProfileByUsername(username));
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar mi perfil (con imágenes)")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @ModelAttribute ProfileRequest request,
            Principal principal) {
        
        return ResponseEntity.ok(service.updateMyProfile(principal.getName(), request));
    }
    
    
    
}
