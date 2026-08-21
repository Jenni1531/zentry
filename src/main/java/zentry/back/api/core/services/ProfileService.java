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

    @Getter
    @AllArgsConstructor
    public static class FollowResult {
        private final boolean following;
        private final int followersCount;

        public boolean isFollowing() {
            return following;
        }

        public int getNewFollowersCount() {
            return followersCount;
        }
    }

    @Transactional
    public FollowResult toggleFollowUser(String followerIdentifier, String targetIdentifier) {
        User followerUser = userRepo.findByUsernameOrEmail(followerIdentifier, followerIdentifier)
                .orElseGet(() -> userRepo.findByEmailStartingWith(followerIdentifier + "@")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario seguidor no encontrado")));

        User targetUser = userRepo.findByUsernameOrEmail(targetIdentifier, targetIdentifier)
                .orElseGet(() -> userRepo.findByEmailStartingWith(targetIdentifier + "@")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario a seguir no encontrado")));

        if (followerUser.getId().equals(targetUser.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes seguirte a ti mismo");
        }

        boolean alreadyFollowing = followRepo.existsByFollowerAndFollowing(followerUser.getId(), targetUser.getId());
        boolean isFollowing;
        if (alreadyFollowing) {
            followRepo.deleteByFollowerAndFollowing(followerUser.getId(), targetUser.getId());
            isFollowing = false;
        } else {
            followRepo.save(Follow.builder()
                    .follower(followerUser.getId())
                    .following(targetUser.getId())
                    .build());
            isFollowing = true;
        }

        long followersCount = followRepo.countByFollowing(targetUser.getId());
        return new FollowResult(isFollowing, (int) followersCount);
    }

    @Transactional
    public boolean toggleFollow(String followerIdentifier, String targetIdentifier) {
        return toggleFollowUser(followerIdentifier, targetIdentifier).isFollowing();
    }

    public Profile findOrCreateByUsername(String usernameOrEmail) {
        User user = userRepo.findByEmail(usernameOrEmail)
                .orElseGet(() -> userRepo.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseGet(() -> userRepo.findByEmailStartingWith(usernameOrEmail + "@")
                .orElseGet(() -> userRepo.findByEmailStartingWith(usernameOrEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))))));

        return profileRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    Profile newP = Profile.builder().userId(user.getId()).build();
                    return profileRepo.save(newP);
                });
    }

    @Transactional
    public ProfileResponse updateProfile(String emailOrUsername, ProfileRequest request) {
        return updateMyProfile(emailOrUsername, request);
    }

    @Transactional
    public ProfileResponse updateProfileWithFiles(String emailOrUsername, ProfileRequest request) {
        return updateMyProfile(emailOrUsername, request);
    }

    @Transactional
    public ProfileResponse updateMyProfile(String emailOrUsername, ProfileRequest request) {
        User user = userRepo.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepo.findByUsername(emailOrUsername)
                .orElseGet(() -> userRepo.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseGet(() -> userRepo.findByEmailStartingWith(emailOrUsername + "@")
                .orElseGet(() -> userRepo.findByEmailStartingWith(emailOrUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))))));

        Profile profile = findOrCreateByUsername(emailOrUsername);

        if (request.getName() != null && !request.getName().isBlank()) profile.setName(request.getName());
        if (request.getDiscipline() != null) profile.setDiscipline(request.getDiscipline());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBio() != null) profile.setBio(request.getBio());

        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBannerUrl() != null && !request.getBannerUrl().isBlank()) {
            profile.setBannerUrl(request.getBannerUrl());
        }

        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = saveImage(request.getAvatar(), "profiles");
            if (avatarUrl != null) {
                profile.setAvatarUrl(avatarUrl);
            }
        }

        if (request.getBanner() != null && !request.getBanner().isEmpty()) {
            String bannerUrl = saveImage(request.getBanner(), "profiles");
            if (bannerUrl != null) {
                profile.setBannerUrl(bannerUrl);
            }
        }

        Profile savedProfile = profileRepo.save(profile);
        
        long followersCount = followRepo.countByFollowing(user.getId());
        long followingCount = followRepo.countByFollower(user.getId());

        ProfileResponse response = mapToResponse(user, savedProfile);
        response.setFollowersCount((int) followersCount);
        response.setFollowingCount((int) followingCount);
        response.setIsFollowing(false);
        return response;
    }

    // MÉTODO SEGURO PARA GUARDAR EN DISCO
    public String saveImage(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path uploadDir = Paths.get("uploads", subfolder);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String extension = ".png";
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID().toString() + extension;
            Path targetPath = uploadDir.resolve(fileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + subfolder + "/" + fileName;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar la imagen: " + e.getMessage(), e);
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
