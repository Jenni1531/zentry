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
import zentry.back.api.core.models.Follow;
import zentry.back.api.core.repositories.FollowRepository;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.core.repositories.UserRepository;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings("null")
public class ProfileService {

    private final ProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final FollowRepository followRepo;

    public ProfileService(ProfileRepository profileRepo, UserRepository userRepo, FollowRepository followRepo) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
        this.followRepo = followRepo;
    }

    public ProfileResponse getProfileByUsername(String identifier) {
        return getProfileByUsername(identifier, null);
    }

    public ProfileResponse getProfileByUsername(String identifier, String currentUsername) {
        User user = userRepo.findByUsernameOrEmail(identifier, identifier)
                .orElseGet(() -> userRepo.findByEmail(identifier)
                .orElseGet(() -> userRepo.findByEmailStartingWith(identifier + "@")
                .orElseGet(() -> userRepo.findByEmailStartingWith(identifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado")))));

        Profile profile = profileRepo.findByUserId(user.getId())
                .orElse(new Profile());

        boolean isFollowing = false;
        if (currentUsername != null && !currentUsername.isBlank()) {
            User currentUser = userRepo.findByUsernameOrEmail(currentUsername, currentUsername)
                    .orElseGet(() -> userRepo.findByEmail(currentUsername).orElse(null));
            if (currentUser != null) {
                isFollowing = followRepo.existsByFollowerAndFollowing(currentUser.getId(), user.getId());
            }
        }

        long followersCount = followRepo.countByFollowing(user.getId());
        long followingCount = followRepo.countByFollower(user.getId());

        ProfileResponse response = mapToResponse(user, profile);
        response.setFollowersCount((int) followersCount);
        response.setFollowingCount((int) followingCount);
        response.setIsFollowing(isFollowing);

        return response;
    }

    @Transactional
    public boolean toggleFollow(String followerIdentifier, String targetIdentifier) {
        User followerUser = userRepo.findByUsernameOrEmail(followerIdentifier, followerIdentifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario seguidor no encontrado"));

        User targetUser = userRepo.findByUsernameOrEmail(targetIdentifier, targetIdentifier)
                .orElseGet(() -> userRepo.findByEmailStartingWith(targetIdentifier + "@")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario a seguir no encontrado")));

        if (followerUser.getId().equals(targetUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes seguirte a ti mismo");
        }

        boolean alreadyFollowing = followRepo.existsByFollowerAndFollowing(followerUser.getId(), targetUser.getId());
        if (alreadyFollowing) {
            followRepo.deleteByFollowerAndFollowing(followerUser.getId(), targetUser.getId());
            return false;
        } else {
            followRepo.save(Follow.builder()
                    .follower(followerUser.getId())
                    .following(targetUser.getId())
                    .build());
            return true;
        }
    }

    @Transactional
    public ProfileResponse updateMyProfile(String emailOrUsername, ProfileRequest request) {
        User user = userRepo.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepo.findByUsername(emailOrUsername)
                .orElseGet(() -> userRepo.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))));

        Profile profile = profileRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    Profile newP = Profile.builder().userId(user.getId()).build();
                    return profileRepo.save(newP);
                });

        if (request.getName() != null) profile.setName(request.getName());
        if (request.getDiscipline() != null) profile.setDiscipline(request.getDiscipline());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBio() != null) profile.setBio(request.getBio());

        String safeUsername = user.getUsername() != null ? user.getUsername() : user.getEmail().split("@")[0];

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarName = saveImage(request.getAvatar(), safeUsername, "avatar");
            profile.setAvatarUrl("/uploads/profiles/" + avatarName);
        }

        if (request.getBanner() != null && !request.getBanner().isEmpty()) {
            String bannerName = saveImage(request.getBanner(), safeUsername, "banner");
            profile.setBannerUrl("/uploads/profiles/" + bannerName);
        }

        Profile savedProfile = profileRepo.save(profile);
        return getProfileByUsername(safeUsername, emailOrUsername);
    }

    // MÉTODO INTERNO PARA GUARDAR EN DISCO
    private String saveImage(MultipartFile file, String username, String type) {
        try {
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
        List<Profile> profiles = profileRepo.findByNameContainingIgnoreCase(query);
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
                .isFollowing(false)
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
