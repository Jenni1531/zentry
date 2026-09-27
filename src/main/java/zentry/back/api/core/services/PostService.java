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
import zentry.back.api.core.utils.SecurityUtils;

import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.models.Bookmark;
import zentry.back.api.core.models.Profile;
import zentry.back.api.core.models.User;
import zentry.back.api.core.models.Post;
import zentry.back.api.core.models.PostLike;
import zentry.back.api.core.repositories.BookmarkRepository;
import zentry.back.api.core.repositories.CommentRepository;
import zentry.back.api.core.repositories.CommentReactionRepository;
import zentry.back.api.core.models.Comment;
import org.springframework.transaction.annotation.Transactional;
import zentry.back.api.core.repositories.PostLikeRepository;
import zentry.back.api.core.repositories.PostRepository;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.core.repositories.PostMediaRepository;
import zentry.back.api.core.repositories.StudioProjectRepository;
import zentry.back.api.core.models.StudioProject;
import zentry.back.api.core.models.ContentType;
import zentry.back.api.core.util.ReactionTypes;

@Service
@SuppressWarnings("null")
public class PostService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final PostLikeRepository postLikeRepo;
    private final CommentRepository commentRepo;
    private final ProfileRepository profileRepo;
    private final BookmarkRepository bookmarkRepo;
    private final PostMediaRepository postMediaRepo;
    private final StudioProjectRepository studioProjectRepo;
    private final GamificationEventService gamificationEventService;
    private final NotificationService notificationService;
    private final CommentReactionRepository commentReactionRepo;
    private final CosmeticsService cosmeticsService;

    public PostService(PostRepository postRepo, UserRepository userRepo, PostLikeRepository postLikeRepo,
                        CommentRepository commentRepo, ProfileRepository profileRepo, BookmarkRepository bookmarkRepo,
                        PostMediaRepository postMediaRepo, StudioProjectRepository studioProjectRepo,
                        GamificationEventService gamificationEventService, NotificationService notificationService, CommentReactionRepository commentReactionRepo,
                        CosmeticsService cosmeticsService) {
        this.commentReactionRepo = commentReactionRepo;
        this.cosmeticsService = cosmeticsService;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.postLikeRepo = postLikeRepo;
        this.commentRepo = commentRepo;
        this.profileRepo = profileRepo;
        this.bookmarkRepo = bookmarkRepo;
        this.postMediaRepo = postMediaRepo;
        this.studioProjectRepo = studioProjectRepo;
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
        return mapToResponses(postRepo.findByUserIdOrderByCreatedAtDesc(user.getId()), user.getId());
    }

    public List<PostResponse> getPostsByUsername(String username, String viewerIdentifier) {
        User targetUser = findUserByIdentifier(username);
        Integer viewerId = resolveViewerId(viewerIdentifier);
        return mapToResponses(postRepo.findByUserIdOrderByCreatedAtDesc(targetUser.getId()), viewerId);
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

        List<Integer> ids = postLikeRepo.findByUserIdOrderByCreatedAtDesc(targetUser.getId()).stream()
                .map(PostLike::getPostId).collect(Collectors.toList());
        return mapToResponses(loadInOrder(ids), viewerId);
    }

    public List<PostResponse> getSavedPosts(String username, String viewerIdentifier) {
        User targetUser = findUserByIdentifier(username);
        Integer viewerId = resolveViewerId(viewerIdentifier);

        if (!canViewPrivateList(targetUser, viewerId, Profile::getShowSavedPosts)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este usuario mantiene privados sus guardados");
        }

        List<Integer> ids = bookmarkRepo.findByUserIdOrderByIdDesc(targetUser.getId()).stream()
                .map(Bookmark::getPostId).collect(Collectors.toList());
        return mapToResponses(loadInOrder(ids), viewerId);
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
        return toResponsePage(postRepo.findFeed(pageable), viewerId);
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
        return toResponsePage(postRepo.findByCommunityIdOrderByCreatedAtDesc(communityId, pageable), viewerId);
    }

    /** Explorar: obras con más reacciones de los últimos 30 días */
    public List<PostResponse> getPopular(int page, int size, String viewerIdentifier) {
        java.time.LocalDateTime since = java.time.LocalDateTime.now().minusDays(30);
        List<Post> posts = postRepo.findPopularSince(since, size, Math.max(0, page) * size);
        if (posts.isEmpty() && page == 0) {
            // Plataforma nueva o mes tranquilo: mostrar lo más reciente
            posts = postRepo.findFeed(org.springframework.data.domain.PageRequest.of(0, size)).getContent();
        }
        return mapToResponses(posts, resolveViewerId(viewerIdentifier));
    }

    public List<PostResponse> searchPosts(String query, String viewerIdentifier) {
        if (query == null || query.isBlank()) return List.of();
        return mapToResponses(postRepo.searchPublic(query.trim()), resolveViewerId(viewerIdentifier));
    }

    private Page<PostResponse> toResponsePage(Page<Post> page, Integer viewerId) {
        return new org.springframework.data.domain.PageImpl<>(mapToResponses(page.getContent(), viewerId), page.getPageable(), page.getTotalElements());
    }

    /** Carga publicaciones por id en una consulta, respetando el orden de la lista */
    private List<Post> loadInOrder(List<Integer> ids) {
        java.util.Map<Integer, Post> byId = postRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(Post::getId, p -> p, (x, y) -> x));
        return ids.stream().map(byId::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Integer resolveViewerId(String viewerIdentifier) {
        User viewer = tryFindUser(viewerIdentifier);
        return viewer != null ? viewer.getId() : null;
    }

    public PostResponse create(String identifier, PostRequest request) {
        return create(identifier, request, null);
    }

    public PostResponse create(String identifier, PostRequest request, Integer communityId) {
        return create(identifier, request, communityId, true);
    }

    /**
     * @param syncToStudio false cuando el post nace de un proyecto del Estudio que ya existe
     *                     (evita duplicarlo en el Estudio al publicarlo).
     */
    public PostResponse create(String identifier, PostRequest request, Integer communityId, boolean syncToStudio) {
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
            if (request.getThumbnailUrl().startsWith("data:")) {
                String savedPath = saveBase64Image(request.getThumbnailUrl(), filenameSafeHandle(user), "thumb");
                if (savedPath != null) {
                    post.setThumbnailUrl(savedPath);
                    post.setImageUrl(savedPath);
                }
            } else {
                post.setThumbnailUrl(request.getThumbnailUrl());
                if (post.getImageUrl() == null) {
                    post.setImageUrl(request.getThumbnailUrl());
                }
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            if (request.getImageUrl().startsWith("data:")) {
                String savedPath = saveBase64Image(request.getImageUrl(), filenameSafeHandle(user), "post");
                if (savedPath != null) {
                    post.setImageUrl(savedPath);
                    if (post.getThumbnailUrl() == null) {
                        post.setThumbnailUrl(savedPath);
                    }
                }
            } else {
                post.setImageUrl(request.getImageUrl());
            }
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

        // Procesar archivos multimedia adicionales (fotos, videos, audios)
        List<MultipartFile> allFiles = new ArrayList<>();
        if (request.getFiles() != null && request.getFiles().length > 0) {
            allFiles.addAll(java.util.Arrays.asList(request.getFiles()));
        }
        if (request.getMediaFiles() != null && !request.getMediaFiles().isEmpty()) {
            allFiles.addAll(request.getMediaFiles());
        }

        for (MultipartFile file : allFiles) {
            if (file != null && !file.isEmpty()) {
                String mediaFilename = saveMediaFile(file, filenameSafeHandle(user), "media");
                String mediaUrl = "/uploads/posts/" + mediaFilename;
                postMediaRepo.save(zentry.back.api.core.models.PostMedia.builder()
                        .postId(saved.getId())
                        .url(mediaUrl)
                        .build());
                if (saved.getImageUrl() == null) {
                    saved.setImageUrl(mediaUrl);
                }
                if (saved.getThumbnailUrl() == null) {
                    saved.setThumbnailUrl(mediaUrl);
                }
            }
        }
        saved = postRepo.save(saved);

        // Sincronizar simultáneamente la obra creada desde el feed con el Estudio del usuario
        if (syncToStudio) {
            syncPostToStudio(user, saved, request.getContentType());
        }

        gamificationEventService.recordMissionProgress(user.getId(), "create_post", 1);
        gamificationEventService.recordAchievementProgress(user.getId(), "create_first_project", 1);

        return mapToResponse(user, saved, user.getId());
    }

    private void syncPostToStudio(User user, Post saved, String requestContentType) {
        try {
            String ct = requestContentType != null ? requestContentType.toLowerCase() : "image";
            String mediaPath = saved.getImageUrl() != null ? saved.getImageUrl() : saved.getThumbnailUrl();

            ContentType studioType = ContentType.IMAGE;
            if (ct.contains("audio") || (mediaPath != null && mediaPath.matches("(?i).*\\.(mp3|wav|ogg|m4a|aac|weba|flac)$"))) {
                studioType = ContentType.AUDIO;
            } else if (ct.contains("video") || (mediaPath != null && mediaPath.matches("(?i).*\\.(mp4|webm|mov|mkv)$"))) {
                studioType = ContentType.VIDEO;
            } else if (ct.contains("document") || ct.contains("text")) {
                studioType = ContentType.DOCUMENT;
            } else if (ct.contains("canvas")) {
                studioType = ContentType.CANVAS;
            }

            String title = saved.getTitle() != null && !saved.getTitle().isBlank() ? saved.getTitle() : "Publicación del Feed";

            StudioProject studioProject = StudioProject.builder()
                    .title(truncate(title, 200))
                    .description(truncate(saved.getContenido(), 1000))
                    .type(studioType)
                    .contentData(studioType == ContentType.DOCUMENT ? saved.getContenido() : null)
                    .mediaUrl(mediaPath)
                    // Copia propia: Hibernate no permite que dos entidades compartan la misma colección
                    .tools(saved.getTools() != null ? new ArrayList<>(saved.getTools()) : new ArrayList<>())
                    .rewardCoins(50)
                    .ownerUsername(user.getHandle() != null ? user.getHandle() : user.getEmail())
                    .published(true)
                    .postId(saved.getId())
                    .build();

            studioProjectRepo.save(studioProject);
        } catch (Exception e) {
            log.warn("No se pudo sincronizar el post {} con el Estudio: {}", saved.getId(), e.getMessage(), e);
        }
    }

    private static String truncate(String value, int max) {
        if (value == null || value.length() <= max) return value;
        return value.substring(0, max);
    }

    public PostResponse update(Integer id, PostRequest request) {
        return update(id, null, request);
    }

    @Transactional
    public PostResponse update(Integer id, String identifier, PostRequest request) {
        String activeIdentifier = (identifier != null && !identifier.isBlank()) ? identifier : SecurityUtils.getCurrentUserEmail();
        User user = findUserByIdentifier(activeIdentifier);

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
            if (request.getThumbnailUrl().startsWith("data:")) {
                String savedPath = saveBase64Image(request.getThumbnailUrl(), filenameSafeHandle(user), "thumb");
                if (savedPath != null) {
                    post.setThumbnailUrl(savedPath);
                    post.setImageUrl(savedPath);
                }
            } else {
                post.setThumbnailUrl(request.getThumbnailUrl());
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            if (request.getImageUrl().startsWith("data:")) {
                String savedPath = saveBase64Image(request.getImageUrl(), filenameSafeHandle(user), "post");
                if (savedPath != null) {
                    post.setImageUrl(savedPath);
                    if (post.getThumbnailUrl() == null) {
                        post.setThumbnailUrl(savedPath);
                    }
                }
            } else {
                post.setImageUrl(request.getImageUrl());
            }
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageName = saveMediaFile(request.getImage(), filenameSafeHandle(user), "post");
            post.setImageUrl("/uploads/posts/" + imageName);
        }

        if (request.getTools() != null && !request.getTools().isEmpty()) {
            post.setTools(java.util.Arrays.asList(request.getTools().split(",")));
        }

        List<MultipartFile> allFiles = new ArrayList<>();
        if (request.getFiles() != null && request.getFiles().length > 0) {
            allFiles.addAll(java.util.Arrays.asList(request.getFiles()));
        }
        if (request.getMediaFiles() != null && !request.getMediaFiles().isEmpty()) {
            allFiles.addAll(request.getMediaFiles());
        }

        for (MultipartFile file : allFiles) {
            if (file != null && !file.isEmpty()) {
                String mediaFilename = saveMediaFile(file, filenameSafeHandle(user), "media");
                postMediaRepo.save(zentry.back.api.core.models.PostMedia.builder()
                        .postId(post.getId())
                        .url("/uploads/posts/" + mediaFilename)
                        .build());
            }
        }

        post.setUpdatedAt(java.time.LocalDateTime.now());
        Post saved = postRepo.save(post);

        // Mantener el título/descripción del proyecto enlazado en el Estudio
        for (StudioProject proj : studioProjectRepo.findByPostId(saved.getId())) {
            if (saved.getTitle() != null && !saved.getTitle().isBlank()) proj.setTitle(truncate(saved.getTitle(), 200));
            proj.setDescription(truncate(saved.getContenido(), 1000));
            studioProjectRepo.save(proj);
        }

        return mapToResponse(user, saved, user.getId());
    }

    public void delete(Integer id) {
        delete(id, null);
    }

    @Transactional
    public void delete(Integer id, String identifier) {
        String activeIdentifier = (identifier != null && !identifier.isBlank()) ? identifier : SecurityUtils.getCurrentUserEmail();
        User user = findUserByIdentifier(activeIdentifier);

        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));

        if (!post.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta obra");
        }

        // 1. Limpiar comentarios y sus reacciones
        List<Comment> comments = commentRepo.findByPostIdOrderByCreatedAtAsc(id);
        if (!comments.isEmpty()) {
            List<Integer> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());
            try {
                commentReactionRepo.deleteByCommentIdIn(commentIds);
            } catch (Exception e) {
                log.warn("Error al eliminar reacciones de comentarios de post {}: {}", id, e.getMessage());
            }
            commentRepo.deleteByPostId(id);
        }

        // 2. Limpiar likes, guardados y multimedia
        postLikeRepo.deleteByPostId(id);
        bookmarkRepo.deleteByPostId(id);
        postMediaRepo.deleteByPostId(id);

        // 3. El proyecto del Estudio se conserva como borrador (el usuario no pierde su obra)
        for (StudioProject proj : studioProjectRepo.findByPostId(id)) {
            proj.setPostId(null);
            proj.setPublished(false);
            studioProjectRepo.save(proj);
        }

        // 4. Eliminar el post
        postRepo.delete(post);
    }

    /** Botón clásico de me gusta: si ya reaccionó (con cualquier emoji) la quita, si no pone ❤️. */
    @Transactional
    public PostResponse toggleLike(Integer id, String identifier) {
        User liker = findUserByIdentifier(identifier);
        if (postLikeRepo.existsByPostIdAndUserId(id, liker.getId())) {
            Post post = postRepo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));
            postLikeRepo.deleteByPostIdAndUserId(id, liker.getId());
            User author = userRepo.findById(post.getUserId()).orElse(new User());
            return mapToResponse(author, post, liker.getId());
        }
        return react(id, identifier, ReactionTypes.DEFAULT);
    }

    /**
     * Reaccionar con un emoji: misma reacción = la quita, otra distinta = la cambia,
     * sin reacción previa = la crea (cuenta para misiones/logros y notifica al autor).
     */
    @Transactional
    public PostResponse react(Integer id, String identifier, String reactionType) {
        String type = ReactionTypes.normalize(reactionType);
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));
        User liker = findUserByIdentifier(identifier);
        User author = userRepo.findById(post.getUserId()).orElse(new User());

        PostLike existing = postLikeRepo.findByPostIdAndUserId(id, liker.getId()).orElse(null);
        if (existing != null) {
            if (type.equals(ReactionTypes.orDefault(existing.getReactionType()))) {
                postLikeRepo.deleteByPostIdAndUserId(id, liker.getId());
            } else {
                existing.setReactionType(type);
                postLikeRepo.save(existing);
            }
            return mapToResponse(author, post, liker.getId());
        }

        postLikeRepo.save(PostLike.builder()
                .postId(id)
                .userId(liker.getId())
                .reactionType(type)
                .createdAt(java.time.LocalDateTime.now())
                .build());

        gamificationEventService.recordMissionProgress(liker.getId(), "react_posts", 1);
        gamificationEventService.recordAchievementProgress(liker.getId(), "like_posts", 1);

        long reactionsOnPost = postLikeRepo.countByPostId(id);
        gamificationEventService.setAchievementProgressAbsolute(author.getId(), "post_reactions", (int) reactionsOnPost);

        if (!liker.getId().equals(author.getId())) {
            Profile likerProfile = profileRepo.findByUserId(liker.getId()).orElse(null);
            String text = ReactionTypes.DEFAULT.equals(type)
                    ? " le gustó tu publicación \"" + post.getTitle() + "\""
                    : " reaccionó " + ReactionTypes.emoji(type) + " a tu publicación \"" + post.getTitle() + "\"";
            notificationService.notify(
                    author.getId(),
                    "like",
                    "@" + filenameSafeHandle(liker) + text,
                    liker.getHandle(),
                    likerProfile != null ? likerProfile.getAvatarUrl() : null,
                    post.getId()
            );
        }

        return mapToResponse(author, post, liker.getId());
    }

    private String saveImage(MultipartFile file, String username, String type) {
        return saveMediaFile(file, username, type);
    }

    private String saveMediaFile(MultipartFile file, String username, String type) {
        try {
            Path uploadPath = Paths.get("uploads/posts");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = ".bin";
            String originalExt = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                    : "";
            // Solo extensiones simples (evita nombres raros o rutas en el nombre original)
            if (originalExt.matches("\\.[a-z0-9]{1,5}")) {
                extension = originalExt;
            } else if (file.getContentType() != null) {
                String ct = file.getContentType().toLowerCase();
                if (ct.contains("png")) extension = ".png";
                else if (ct.contains("jpeg") || ct.contains("jpg")) extension = ".jpg";
                else if (ct.contains("webp")) extension = ".webp";
                else if (ct.contains("mp4")) extension = ".mp4";
                else if (ct.contains("webm")) extension = ".webm";
                else if (ct.contains("mp3") || ct.contains("mpeg")) extension = ".mp3";
                else if (ct.contains("wav")) extension = ".wav";
                else if (ct.contains("ogg")) extension = ".ogg";
            }

            String newFilename = username + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el archivo multimedia de la obra", e);
        }
    }

    private String saveBase64Image(String base64Data, String username, String type) {
        if (base64Data == null || base64Data.isBlank()) return null;
        if (!base64Data.startsWith("data:")) return base64Data;
        try {
            Path uploadPath = Paths.get("uploads/posts");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String base64Image = base64Data;
            String extension = ".png";
            if (base64Data.contains(",")) {
                String[] parts = base64Data.split(",", 2);
                String header = parts[0].toLowerCase();
                if (header.contains("image/jpeg") || header.contains("image/jpg")) {
                    extension = ".jpg";
                } else if (header.contains("image/webp")) {
                    extension = ".webp";
                } else if (header.contains("image/gif")) {
                    extension = ".gif";
                } else if (header.contains("video/mp4")) {
                    extension = ".mp4";
                } else if (header.contains("video/webm")) {
                    extension = ".webm";
                } else if (header.contains("video/quicktime") || header.contains("video/mov")) {
                    extension = ".mov";
                } else if (header.contains("video/x-matroska") || header.contains("video/mkv")) {
                    extension = ".mkv";
                } else if (header.contains("video/x-msvideo") || header.contains("video/avi")) {
                    extension = ".avi";
                } else if (header.contains("video/")) {
                    extension = ".mp4";
                } else if (header.contains("audio/mpeg") || header.contains("audio/mp3")) {
                    extension = ".mp3";
                } else if (header.contains("audio/wav") || header.contains("audio/x-wav")) {
                    extension = ".wav";
                } else if (header.contains("audio/ogg")) {
                    extension = ".ogg";
                } else if (header.contains("audio/m4a") || header.contains("audio/x-m4a")
                        || header.contains("audio/mp4") || header.contains("audio/aac")) {
                    extension = ".m4a";
                } else if (header.contains("audio/webm")) {
                    extension = ".weba";
                } else if (header.contains("audio/flac") || header.contains("audio/x-flac")) {
                    extension = ".flac";
                } else if (header.contains("application/pdf")) {
                    extension = ".pdf";
                }
                base64Image = parts[1];
            }

            byte[] decodedBytes = Base64.getDecoder().decode(base64Image.trim());
            String newFilename = username + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path filePath = uploadPath.resolve(newFilename);
            Files.write(filePath, decodedBytes);

            return "/uploads/posts/" + newFilename;
        } catch (Exception e) {
            return null;
        }
    }

    private PostResponse mapToResponse(User user, Post post, Integer viewerUserId) {
        return mapToResponses(List.of(post), viewerUserId).get(0);
    }

    /**
     * Mapea varias publicaciones con un número FIJO de consultas (autores, perfiles, reacciones,
     * comentarios, guardados, multimedia y cosméticos se cargan en lote), en lugar de ~8 por obra.
     * Con la base remota (Neon) esto es lo que más acelera el feed.
     */
    private List<PostResponse> mapToResponses(List<Post> posts, Integer viewerUserId) {
        if (posts.isEmpty()) return List.of();

        List<Integer> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        java.util.Set<Integer> authorIds = posts.stream().map(Post::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());

        java.util.Map<Integer, User> authors = userRepo.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u, (x, y) -> x));
        java.util.Map<Integer, Profile> profiles = profileRepo.findByUserIdIn(authorIds).stream()
                .collect(Collectors.toMap(Profile::getUserId, pr -> pr, (x, y) -> x));
        java.util.Map<Integer, zentry.back.api.core.dtos.CosmeticsResponse> cosmetics = cosmeticsService.forUsers(authorIds);

        java.util.Map<Integer, Long> commentCounts = new java.util.HashMap<>();
        for (Object[] row : commentRepo.countByPostIds(postIds)) {
            commentCounts.put((Integer) row[0], ((Number) row[1]).longValue());
        }

        java.util.Map<Integer, java.util.Map<String, Integer>> reactionCounts = new java.util.HashMap<>();
        for (Object[] row : postLikeRepo.countByPostIdsAndReactionType(postIds)) {
            reactionCounts.computeIfAbsent((Integer) row[0], k -> new java.util.LinkedHashMap<>())
                    .merge(ReactionTypes.orDefault((String) row[1]), ((Number) row[2]).intValue(), Integer::sum);
        }

        java.util.Map<Integer, String> myReactions = new java.util.HashMap<>();
        java.util.Set<Integer> savedIds = new java.util.HashSet<>();
        if (viewerUserId != null) {
            for (PostLike like : postLikeRepo.findByUserIdAndPostIdIn(viewerUserId, postIds)) {
                myReactions.put(like.getPostId(), ReactionTypes.orDefault(like.getReactionType()));
            }
            for (Bookmark bm : bookmarkRepo.findByUserIdAndPostIdIn(viewerUserId, postIds)) {
                savedIds.add(bm.getPostId());
            }
        }

        java.util.Map<Integer, List<String>> extraMedia = new java.util.HashMap<>();
        for (var pm : postMediaRepo.findByPostIdIn(postIds)) {
            if (pm.getUrl() != null) extraMedia.computeIfAbsent(pm.getPostId(), k -> new ArrayList<>()).add(pm.getUrl());
        }

        List<PostResponse> result = new ArrayList<>(posts.size());
        for (Post post : posts) {
            User author = authors.getOrDefault(post.getUserId(), new User());
            Profile authorProfile = profiles.get(post.getUserId());
            java.util.Map<String, Integer> counts = reactionCounts.getOrDefault(post.getId(), new java.util.LinkedHashMap<>());
            long likesCount = counts.values().stream().mapToLong(Integer::longValue).sum();
            String myReaction = myReactions.get(post.getId());
            boolean isOwner = viewerUserId != null && viewerUserId.equals(post.getUserId());

            List<String> mediaUrls = new ArrayList<>();
            if (post.getImageUrl() != null && !post.getImageUrl().isBlank()) mediaUrls.add(post.getImageUrl());
            for (String url : extraMedia.getOrDefault(post.getId(), List.of())) {
                if (!mediaUrls.contains(url)) mediaUrls.add(url);
            }

            String handle = author.getHandle() != null ? author.getHandle() : "usuario";
            result.add(PostResponse.builder()
                    .id(post.getId())
                    .userId(post.getUserId())
                    .canEdit(isOwner)
                    .canDelete(isOwner)
                    .authorUsername(handle)
                    .authorName(authorProfile != null && authorProfile.getName() != null && !authorProfile.getName().isBlank()
                            ? authorProfile.getName() : handle)
                    .authorAvatar(authorProfile != null ? authorProfile.getAvatarUrl() : null)
                    .authorDiscipline(authorProfile != null ? authorProfile.getDiscipline() : null)
                    .authorCosmetics(cosmetics.get(post.getUserId()))
                    .title(post.getTitle())
                    .contenido(post.getContenido())
                    .contentType(post.getContentType() != null ? post.getContentType() : "canvas")
                    .thumbnailUrl(post.getThumbnailUrl())
                    .imageUrl(post.getImageUrl())
                    .visibility(post.getVisibility())
                    .communityId(post.getCommunityId())
                    .tools(post.getTools() != null ? new ArrayList<>(post.getTools()) : new ArrayList<>())
                    .mediaUrls(mediaUrls)
                    .tags(new ArrayList<>())
                    .likesCount((int) likesCount)
                    .commentsCount(commentCounts.getOrDefault(post.getId(), 0L).intValue())
                    .liked(myReaction != null)
                    .myReaction(myReaction)
                    .reactionCounts(counts)
                    .saved(savedIds.contains(post.getId()))
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build());
        }
        return result;
    }
}
