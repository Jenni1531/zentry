package zentry.back.api.core.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.CommentRequest;
import zentry.back.api.core.dtos.CommentResponse;
import zentry.back.api.core.models.Comment;
import zentry.back.api.core.models.Profile;
import zentry.back.api.core.models.User;
import zentry.back.api.core.repositories.CommentRepository;
import zentry.back.api.core.repositories.PostRepository;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.core.repositories.UserRepository;

import zentry.back.api.core.models.CommentReaction;
import zentry.back.api.core.repositories.CommentReactionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommentService {

    private final CommentRepository repo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;
    private final CommentReactionRepository commentReactionRepo;
    private final CosmeticsService cosmeticsService;
    private final GamificationEventService gamificationEventService;
    private final NotificationService notificationService;

    public CommentService(CommentRepository repo, PostRepository postRepo, UserRepository userRepo,
                           ProfileRepository profileRepo, CommentReactionRepository commentReactionRepo,
                           GamificationEventService gamificationEventService,
                           NotificationService notificationService, CosmeticsService cosmeticsService) {
        this.repo = repo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.commentReactionRepo = commentReactionRepo;
        this.cosmeticsService = cosmeticsService;
        this.gamificationEventService = gamificationEventService;
        this.notificationService = notificationService;
    }

    private User resolveUser(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }
        return userRepo.findByUsernameOrEmail(identifier, identifier)
                .orElseGet(() -> userRepo.findByEmail(identifier)
                .orElseGet(() -> userRepo.findByUsername(identifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"))));
    }

    private User tryResolveUser(String identifier) {
        if (identifier == null || identifier.isBlank()) return null;
        try {
            return resolveUser(identifier);
        } catch (Exception e) {
            return null;
        }
    }

    public List<CommentResponse> listByPost(Integer postId) {
        return listByPost(postId, null);
    }

    public List<CommentResponse> listByPost(Integer postId, String currentIdentifier) {
        User currentUser = tryResolveUser(currentIdentifier);
        // El dueño de la publicación se resuelve una sola vez para toda la lista
        Integer postOwnerId = postRepo.findById(postId).map(zentry.back.api.core.models.Post::getUserId).orElse(null);
        List<Comment> comments = repo.findByPostIdOrderByCreatedAtAsc(postId);
        if (comments.isEmpty()) return List.of();

        // Todo en lote: autores, perfiles, cosméticos, conteos y "me gusta" propios
        List<Integer> ids = comments.stream().map(Comment::getId).collect(Collectors.toList());
        java.util.Set<Integer> authorIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        java.util.Map<Integer, User> authors = userRepo.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));
        java.util.Map<Integer, Profile> profiles = profileRepo.findByUserIdIn(authorIds).stream()
                .collect(Collectors.toMap(Profile::getUserId, p -> p, (a, b) -> a));
        java.util.Map<Integer, zentry.back.api.core.dtos.CosmeticsResponse> cosmetics = cosmeticsService.forUsers(authorIds);
        java.util.Map<Integer, Long> likeCounts = new java.util.HashMap<>();
        for (Object[] row : commentReactionRepo.countByCommentIds(ids)) {
            likeCounts.put((Integer) row[0], ((Number) row[1]).longValue());
        }
        java.util.Set<Integer> likedIds = currentUser == null ? java.util.Set.of()
                : commentReactionRepo.findByUserIdAndCommentIdIn(currentUser.getId(), ids).stream()
                    .map(CommentReaction::getCommentId).collect(Collectors.toSet());

        return comments.stream()
                .map(c -> build(c, authors.getOrDefault(c.getUserId(), new User()), profiles.get(c.getUserId()),
                        cosmetics.get(c.getUserId()), likeCounts.getOrDefault(c.getId(), 0L), likedIds.contains(c.getId()),
                        currentUser, postOwnerId))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse create(Integer postId, String identifier, CommentRequest request) {
        User user = resolveUser(identifier);

        zentry.back.api.core.models.Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada"));

        Comment entity = Comment.builder()
                .postId(postId)
                .userId(user.getId())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        Comment saved = repo.save(entity);

        gamificationEventService.recordMissionProgress(user.getId(), "comment_posts", 1);
        gamificationEventService.recordAchievementProgress(user.getId(), "comment_posts", 1);

        if (!post.getUserId().equals(user.getId())) {
            Profile commenterProfile = profileRepo.findByUserId(user.getId()).orElse(null);
            notificationService.notify(
                    post.getUserId(),
                    "comment",
                    "@" + user.getHandle() + " comentó tu publicación \"" + post.getTitle() + "\"",
                    user.getHandle(),
                    commenterProfile != null ? commenterProfile.getAvatarUrl() : null,
                    post.getId()
            );
        }

        return toResponse(saved, user);
    }

    @Transactional
    public CommentResponse update(Integer id, String identifier, CommentRequest request) {
        User user = resolveUser(identifier);
        Comment entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado"));

        if (!entity.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes editar el comentario de otro usuario");
        }

        if (request.getContent() != null && !request.getContent().isBlank()) {
            entity.setContent(request.getContent());
        }

        Comment updated = repo.save(entity);
        return toResponse(updated, user);
    }

    @Transactional
    public void delete(Integer id, String identifier) {
        User user = resolveUser(identifier);
        Comment entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado"));

        zentry.back.api.core.models.Post post = postRepo.findById(entity.getPostId()).orElse(null);
        boolean isPostOwner = post != null && post.getUserId().equals(user.getId());
        boolean isCommentAuthor = entity.getUserId().equals(user.getId());

        if (!isCommentAuthor && !isPostOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este comentario");
        }

        commentReactionRepo.deleteByCommentId(entity.getId());
        repo.delete(entity);
    }

    @Transactional
    public CommentResponse toggleLike(Integer id, String identifier) {
        User user = resolveUser(identifier);
        Comment comment = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado"));

        boolean alreadyLiked = commentReactionRepo.existsByCommentIdAndUserId(comment.getId(), user.getId());
        if (alreadyLiked) {
            commentReactionRepo.deleteByCommentIdAndUserId(comment.getId(), user.getId());
        } else {
            commentReactionRepo.save(CommentReaction.builder()
                    .commentId(comment.getId())
                    .userId(user.getId())
                    .build());

            if (!comment.getUserId().equals(user.getId())) {
                Profile userProfile = profileRepo.findByUserId(user.getId()).orElse(null);
                notificationService.notify(
                        comment.getUserId(),
                        "comment_like",
                        "@" + user.getHandle() + " le dio me gusta a tu comentario",
                        user.getHandle(),
                        userProfile != null ? userProfile.getAvatarUrl() : null,
                        comment.getPostId()
                );
            }
        }

        return toResponse(comment, user);

    }

    private CommentResponse toResponse(Comment entity, User currentUser) {
        Integer postOwnerId = postRepo.findById(entity.getPostId()).map(zentry.back.api.core.models.Post::getUserId).orElse(null);
        return toResponse(entity, currentUser, postOwnerId);
    }

    private CommentResponse toResponse(Comment entity, User currentUser, Integer postOwnerId) {
        User author = userRepo.findById(entity.getUserId()).orElse(new User());
        Profile profile = profileRepo.findByUserId(entity.getUserId()).orElse(null);
        long likesCount = commentReactionRepo.countByCommentId(entity.getId());
        boolean liked = currentUser != null && commentReactionRepo.existsByCommentIdAndUserId(entity.getId(), currentUser.getId());
        return build(entity, author, profile, cosmeticsService.forUser(entity.getUserId()), likesCount, liked, currentUser, postOwnerId);
    }

    private CommentResponse build(Comment entity, User author, Profile profile, zentry.back.api.core.dtos.CosmeticsResponse cosmetics,
                                  long likesCount, boolean liked, User currentUser, Integer postOwnerId) {
        boolean isCommentAuthor = currentUser != null && currentUser.getId().equals(entity.getUserId());
        boolean isPostOwner = currentUser != null && postOwnerId != null && currentUser.getId().equals(postOwnerId);

        return CommentResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .userId(entity.getUserId())
                .authorUsername(author.getHandle() != null ? author.getHandle() : author.getEmail())
                .authorAvatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .authorCosmetics(cosmetics)
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .likesCount((int) likesCount)
                .liked(liked)
                .canEdit(isCommentAuthor)
                .canDelete(isCommentAuthor || isPostOwner)
                .build();
    }
}
