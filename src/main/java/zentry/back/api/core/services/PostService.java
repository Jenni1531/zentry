package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.models.Bookmark;
import zentry.back.api.core.models.Profile;
import zentry.back.api.core.models.User;
import zentry.back.api.core.models.Post;
import zentry.back.api.core.models.PostLike;
import zentry.back.api.core.repositories.BookmarkRepository;
import zentry.back.api.core.repositories.CommentRepository;
import zentry.back.api.core.repositories.PostLikeRepository;
import zentry.back.api.core.repositories.PostRepository;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.core.repositories.UserRepository;

@Service
@SuppressWarnings("null")
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final PostLikeRepository postLikeRepo;
    private final CommentRepository commentRepo;
    private final ProfileRepository profileRepo;
    private final BookmarkRepository bookmarkRepo;
    private final GamificationEventService gamificationEventService;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepo, UserRepository userRepo, PostLikeRepository postLikeRepo,
                        CommentRepository commentRepo, ProfileRepository profileRepo, BookmarkRepository bookmarkRepo,
                        GamificationEventService gamificationEventService, NotificationService notificationService) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.postLikeRepo = postLikeRepo;
        this.commentRepo = commentRepo;
        this.profileRepo = profileRepo;
        this.bookmarkRepo = bookmarkRepo;
        this.gamificationEventService = gamificationEventService;
        this.notificationService = notificationService;
    }

    private String filenameSafeHandle(User user) {
        return user.getHandle() != null && !user.getHandle().isBlank() ? user.getHandle() : "user";
    }

    private User tryFindUser(String identifier) {
        if (identifier == null || identifier.isBlank() || "anonimo".equalsIgnoreCase(identifier)) {
            return null;
        }
        try {
            return findUserByIdentifier(identifier);
        } catch (ResponseStatusException e) {
            return null;
        }
    }

    private User findUserByIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank() || "anonimo".equalsIgnoreCase(identifier)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        var byEmail = userRepo.findByEmail(identifier);
        if (byEmail.isPresent()) return byEmail.get();

        var byUsername = userRepo.findByUsername(identifier);
        if (byUsername.isPresent()) return byUsername.get();

        var byBoth = userRepo.findByUsernameOrEmail(identifier, identifier);
        if (byBoth.isPresent()) return byBoth.get();

        var byEmailPrefix = userRepo.findByEmailStartingWith(identifier + "@");
        if (byEmailPrefix.isPresent()) return byEmailPrefix.get();

        var byEmailPrefixRaw = userRepo.findByEmailStartingWith(identifier);
        if (byEmailPrefixRaw.isPresent()) return byEmailPrefixRaw.get();

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }

    public List<PostResponse> getMyPosts(String identifier) {
        User user = findUserByIdentifier(identifier);
        List<Post> posts = postRepo.findByUserIdOrderByCreatedAtDesc(user.getId());
        return posts.stream().map(p -> mapToResponse(user, p, user.getId())).collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByUsername(String username, String viewerIdentifier) {
        User targetUser = findUserByIdentifier(username);
        Integer viewerId = resolveViewerId(viewerIdentifier);
        List<Post> posts = postRepo.findByUserIdOrderByCreatedAtDesc(targetUser.getId());
        return posts.stream().map(p -> mapToResponse(targetUser, p, viewerId)).collect(Collectors.toList());
    }

    private boolean canViewPrivateList(User targetUser, Integer viewerId, java.util.function.Function<Profile, Boolean> flagGetter) {
        if (viewerId != null && viewerId.equals(targetUser.getId())) return true;
        Profile profile = profileRepo.findByUserId(targetUser.getId()).orElse(null);
        if (profile == null) return true;
        Boolean flag = flagGetter.apply(profile);
        return !Boolean.FALSE.equals(flag);
    }

    public List<PostResponse> getLikedPosts(String username, String viewerIdentifier) {
        User targetUser = findUserByIdentifier(username);
        Integer viewerId = resolveViewerId(viewerIdentifier);

        if (!canViewPrivateList(targetUser, viewerId, Profile::getShowLikedPosts)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este usuario mantiene privadas sus publicaciones con me gusta");
        }

        List<PostLike> likes = postLikeRepo.findByUserIdOrderByCreatedAtDesc(targetUser.getId());
        return likes.stream()
                .map(like -> postRepo.findById(like.getPostId()).orElse(null))
                .filter(Objects::nonNull)
                .map(post -> {
                    User author = userRepo.findById(post.getUserId()).orElse(new User());
                    return mapToResponse(author, post, viewerId);
                })
                .collect(Collectors.toList());
    }

    public List<PostResponse> getSavedPosts(String username, String viewerIdentifier) {
        User targetUser = findUserByIdentifier(username);
        Integer viewerId = resolveViewerId(viewerIdentifier);

        if (!canViewPrivateList(targetUser, viewerId, Profile::getShowSavedPosts)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este usuario mantiene privados sus guardados");
        }

        List<Bookmark> bookmarks = bookmarkRepo.findByUserIdOrderByIdDesc(targetUser.getId());
        return bookmarks.stream()
                .map(bookmark -> postRepo.findById(bookmark.getPostId()).orElse(null))
                .filter(Objects::nonNull)
                .map(post -> {
                    User author = userRepo.findById(post.getUserId()).orElse(new User());
                    return mapToResponse(author, post, viewerId);
                })
                .collect(Collectors.toList());
    }

    public java.util.Map<String, Boolean> toggleBookmark(Integer postId, String identifier) {
        User user = findUserByIdentifier(identifier);
        if (!postRepo.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada");
        }

        boolean alreadySaved = bookmarkRepo.existsByUserIdAndPostId(user.getId(), postId);
        if (alreadySaved) {
            bookmarkRepo.deleteByUserIdAndPostId(user.getId(), postId);
        } else {
            bookmarkRepo.save(Bookmark.builder().userId(user.getId()).postId(postId).build());
        }
        return java.util.Map.of("saved", !alreadySaved);
    }

    public Page<PostResponse> list(Pageable pageable, String viewerIdentifier) {
        Integer viewerId = resolveViewerId(viewerIdentifier);
        return postRepo.findAllByOrderByCreatedAtDesc(pageable).map(post -> {
            User user = userRepo.findById(post.getUserId()).orElse(new User());
            return mapToResponse(user, post, viewerId);
        });
    }

    public PostResponse getById(Integer id, String viewerIdentifier) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));
        User user = userRepo.findById(post.getUserId()).orElse(new User());

        return mapToResponse(user, post, resolveViewerId(viewerIdentifier));
    }

    public Page<PostResponse> getAllPosts(Pageable pageable, String viewerIdentifier) {
        return list(pageable, viewerIdentifier);
    }

    public Page<PostResponse> getPostsByCommunity(Integer communityId, Pageable pageable, String viewerIdentifier) {
        Integer viewerId = resolveViewerId(viewerIdentifier);
        return postRepo.findByCommunityIdOrderByCreatedAtDesc(communityId, pageable).map(post -> {
            User user = userRepo.findById(post.getUserId()).orElse(new User());
            return mapToResponse(user, post, viewerId);
        });
    }

    private Integer resolveViewerId(String viewerIdentifier) {
        User viewer = tryFindUser(viewerIdentifier);
        return viewer != null ? viewer.getId() : null;
    }

    public PostResponse create(String identifier, PostRequest request) {
        return create(identifier, request, null);
    }

    public PostResponse create(String identifier, PostRequest request, Integer communityId) {
        User user = findUserByIdentifier(identifier);

        Post post = Post.builder()
                .userId(user.getId())
                .title(request.getTitle())
                .contenido(request.getContenido())
                .contentType(request.getContentType() != null ? request.getContentType() : "canvas")
                .visibility(request.getVisibility() != null ? request.getVisibility() : "public")
                .communityId(communityId)
                .build();

        if (request.getThumbnailUrl() != null && !request.getThumbnailUrl().isBlank()) {
            if (request.getThumbnailUrl().startsWith("data:image/")) {
                String savedPath = saveBase64Image(request.getThumbnailUrl(), filenameSafeHandle(user), "thumb");
                post.setThumbnailUrl(savedPath);
                post.setImageUrl(savedPath);
            } else {
                post.setThumbnailUrl(request.getThumbnailUrl());
                if (post.getImageUrl() == null) {
                    post.setImageUrl(request.getThumbnailUrl());
                }
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            post.setImageUrl(request.getImageUrl());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageName = saveImage(request.getImage(), filenameSafeHandle(user), "post");
            post.setImageUrl("/uploads/posts/" + imageName);
            if (post.getThumbnailUrl() == null) {
                post.setThumbnailUrl("/uploads/posts/" + imageName);
            }
        }

        if (request.getTools() != null && !request.getTools().isEmpty()) {
            post.setTools(java.util.Arrays.asList(request.getTools().split(",")));
        }

        Post saved = postRepo.save(post);

        gamificationEventService.recordMissionProgress(user.getId(), "create_post", 1);
        gamificationEventService.recordAchievementProgress(user.getId(), "create_first_project", 1);

        return mapToResponse(user, saved, user.getId());
    }

    public PostResponse update(Integer id, String identifier, PostRequest request) {
        User user = findUserByIdentifier(identifier);

        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));

        if (!post.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar esta obra");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            post.setTitle(request.getTitle());
        }
        if (request.getContenido() != null) {
            post.setContenido(request.getContenido());
        }
        if (request.getContentType() != null) {
            post.setContentType(request.getContentType());
        }
        if (request.getVisibility() != null) {
            post.setVisibility(request.getVisibility());
        }

        if (request.getThumbnailUrl() != null && !request.getThumbnailUrl().isBlank()) {
            if (request.getThumbnailUrl().startsWith("data:image/")) {
                String savedPath = saveBase64Image(request.getThumbnailUrl(), filenameSafeHandle(user), "thumb");
                post.setThumbnailUrl(savedPath);
                post.setImageUrl(savedPath);
            } else {
                post.setThumbnailUrl(request.getThumbnailUrl());
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            post.setImageUrl(request.getImageUrl());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageName = saveImage(request.getImage(), filenameSafeHandle(user), "post");
            post.setImageUrl("/uploads/posts/" + imageName);
        }

        if (request.getTools() != null && !request.getTools().isEmpty()) {
            post.setTools(java.util.Arrays.asList(request.getTools().split(",")));
        }

        post.setUpdatedAt(java.time.LocalDateTime.now());
        return mapToResponse(user, postRepo.save(post), user.getId());
    }

    public void delete(Integer id, String identifier) {
        User user = findUserByIdentifier(identifier);

        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));

        if (!post.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta obra");
        }

        postRepo.delete(post);
    }

    public PostResponse toggleLike(Integer id, String identifier) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));
        User liker = findUserByIdentifier(identifier);
        User author = userRepo.findById(post.getUserId()).orElse(new User());

        boolean alreadyLiked = postLikeRepo.existsByPostIdAndUserId(id, liker.getId());
        if (alreadyLiked) {
            postLikeRepo.deleteByPostIdAndUserId(id, liker.getId());
        } else {
            postLikeRepo.save(PostLike.builder()
                    .postId(id)
                    .userId(liker.getId())
                    .createdAt(java.time.LocalDateTime.now())
                    .build());

            gamificationEventService.recordMissionProgress(liker.getId(), "react_posts", 1);
            gamificationEventService.recordAchievementProgress(liker.getId(), "like_posts", 1);

            long likesOnPost = postLikeRepo.countByPostId(id);
            gamificationEventService.setAchievementProgressAbsolute(author.getId(), "post_reactions", (int) likesOnPost);

            if (!liker.getId().equals(author.getId())) {
                Profile likerProfile = profileRepo.findByUserId(liker.getId()).orElse(null);
                notificationService.notify(
                        author.getId(),
                        "like",
                        "@" + filenameSafeHandle(liker) + " le gustó tu publicación \"" + post.getTitle() + "\"",
                        liker.getHandle(),
                        likerProfile != null ? likerProfile.getAvatarUrl() : null,
                        post.getId()
                );
            }
        }

        return mapToResponse(author, post, liker.getId());
    }

    private String saveImage(MultipartFile file, String username, String type) {
        try {
            Path uploadPath = Paths.get("uploads/posts");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String newFilename = username + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen de la obra", e);
        }
    }

    private String saveBase64Image(String base64Data, String username, String type) {
        try {
            Path uploadPath = Paths.get("uploads/posts");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String base64Image = base64Data;
            String extension = ".png";
            if (base64Data.contains(",")) {
                String[] parts = base64Data.split(",");
                if (parts[0].contains("image/jpeg") || parts[0].contains("image/jpg")) {
                    extension = ".jpg";
                } else if (parts[0].contains("image/webp")) {
                    extension = ".webp";
                }
                base64Image = parts[1];
            }

            byte[] decodedBytes = Base64.getDecoder().decode(base64Image.trim());
            String newFilename = username + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path filePath = uploadPath.resolve(newFilename);
            Files.write(filePath, decodedBytes);

            return "/uploads/posts/" + newFilename;
        } catch (Exception e) {
            return base64Data;
        }
    }

    private PostResponse mapToResponse(User user, Post post, Integer viewerUserId) {
        long likesCount = postLikeRepo.countByPostId(post.getId());
        long commentsCount = commentRepo.countByPostId(post.getId());
        boolean liked = viewerUserId != null && postLikeRepo.existsByPostIdAndUserId(post.getId(), viewerUserId);
        boolean saved = viewerUserId != null && bookmarkRepo.existsByUserIdAndPostId(viewerUserId, post.getId());
        Profile authorProfile = user.getId() != null ? profileRepo.findByUserId(user.getId()).orElse(null) : null;

        return PostResponse.builder()
                .id(post.getId())
                .authorUsername(user.getHandle() != null ? user.getHandle() : "usuario")
                .authorName(user.getHandle() != null ? user.getHandle() : "usuario")
                .authorAvatar(authorProfile != null ? authorProfile.getAvatarUrl() : null)
                .authorDiscipline(authorProfile != null ? authorProfile.getDiscipline() : null)
                .title(post.getTitle())
                .contenido(post.getContenido())
                .contentType(post.getContentType() != null ? post.getContentType() : "canvas")
                .thumbnailUrl(post.getThumbnailUrl())
                .imageUrl(post.getImageUrl())
                .visibility(post.getVisibility())
                .communityId(post.getCommunityId())
                .tools(post.getTools() != null ? post.getTools() : new ArrayList<>())
                .mediaUrls(new ArrayList<>())
                .tags(new ArrayList<>())
                .likesCount((int) likesCount)
                .commentsCount((int) commentsCount)
                .liked(liked)
                .saved(saved)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
