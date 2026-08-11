package zentry.back.api.core.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;
import zentry.back.api.core.dtos.ProfileRequest;
import zentry.back.api.core.dtos.ProfileResponse;
import zentry.back.api.core.models.Profile;
import zentry.back.api.core.models.User;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.core.repositories.UserRepository;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@SuppressWarnings("null")
public class ProfileService {

    private final ProfileRepository profileRepo;
    private final UserRepository userRepo;

    public ProfileService(ProfileRepository profileRepo, UserRepository userRepo) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
    }

    public ProfileResponse getProfileByUsername(String identifier) {
       User user = userRepo.findByUsername(identifier)
                .orElseGet(() -> userRepo.findByEmail(identifier)
                .orElseGet(() -> userRepo.findByEmail(identifier + "@gmail.com")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado en la BD"))));

        Profile profile = profileRepo.findByUserId(user.getId())
                .orElse(new Profile());

        return mapToResponse(user, profile);
    }

    public ProfileResponse updateMyProfile(String email, ProfileRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Profile profile = profileRepo.findByUserId(user.getId())
                .orElse(Profile.builder().userId(user.getId()).build());

        profile.setName(request.getName());
        profile.setDiscipline(request.getDiscipline());
        profile.setLocation(request.getLocation());
        profile.setBio(request.getBio());

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarName = saveImage(request.getAvatar(), user.getUsername(), "avatar");
            profile.setAvatarUrl("/uploads/profiles/" + avatarName);
        }

        if (request.getBanner() != null && !request.getBanner().isEmpty()) {
            String bannerName = saveImage(request.getBanner(), user.getUsername(), "banner");
            profile.setBannerUrl("/uploads/profiles/" + bannerName);
        }

        Profile savedProfile = profileRepo.save(profile);
        return mapToResponse(user, savedProfile);
    }

    // MÉTODO INTERNO PARA GUARDAR EN DISCO
    private String saveImage(MultipartFile file, String username, String type) {
        try {
            // Se creará una carpeta 'uploads/profiles' en la raíz de tu proyecto Spring Boot
            Path uploadPath = Paths.get("uploads/profiles");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = username + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 5) + extension;

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen", e);
        }
    }

    public List<ProfileResponse> searchProfiles(String query) {
        // Buscamos perfiles que coincidan con la búsqueda
        List<Profile> profiles = profileRepo.findByNameContainingIgnoreCase(query);
        
        // Los transformamos en la respuesta que espera el frontend
        return profiles.stream().map(profile -> {
            User user = userRepo.findById(profile.getUserId()).orElse(new User());
            return mapToResponse(user, profile);
        }).toList();
    }

    private ProfileResponse mapToResponse(User user, Profile profile) {
        String displayUsername = user.getUsername() != null ? user.getUsername() : user.getEmail().split("@")[0];
        return ProfileResponse.builder()
                .username(user.getUsername())
                .name(profile.getName() != null ? profile.getName() : displayUsername)
                .discipline(profile.getDiscipline())
                .location(profile.getLocation())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .bannerUrl(profile.getBannerUrl())
                .followersCount(0)
                .followingCount(0)  
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
