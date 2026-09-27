package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.NotificationResponse;
import zentry.back.api.core.models.Notification;
import zentry.back.api.core.models.Profile;
import zentry.back.api.core.models.User;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.core.repositories.NotificationRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.core.mappers.CoreMappers;

import java.time.LocalDateTime;

@Service("coreNotificationService")
@SuppressWarnings("null")
public class NotificationService {

    private final NotificationRepository repo;
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;

    public NotificationService(NotificationRepository repo, UserRepository userRepo, ProfileRepository profileRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    /** Respeta Ajustes > Notificaciones. Sociales, recompensas y logros siempre se envían. */
    private boolean isEnabledFor(Integer recipientUserId, String type) {
        Profile profile = profileRepo.findByUserId(recipientUserId).orElse(null);
        if (profile == null || type == null) return true;
        return switch (type) {
            case "like", "comment_like" -> !Boolean.FALSE.equals(profile.getNotifyReactions());
            case "comment" -> !Boolean.FALSE.equals(profile.getNotifyComments());
            case "message" -> !Boolean.FALSE.equals(profile.getNotifyMessages());
            case "story_reaction", "story_reply" -> !Boolean.FALSE.equals(profile.getNotifyStories());
            default -> true;
        };
    }

    private User resolveUser(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }
        return userRepo.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    /**
     * Lista SOLO las notificaciones del usuario autenticado — nunca las de otros usuarios.
     */
    public Page<NotificationResponse> list(String identifier, Pageable pageable) {
        User user = resolveUser(identifier);
        return repo.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable).map(CoreMappers::toResponse);
    }

    public long countUnread(String identifier) {
        return repo.countByUserIdAndReadFalse(resolveUser(identifier).getId());
    }

    public void markAsRead(Integer id, String identifier) {
        User user = resolveUser(identifier);
        Notification notification = repo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificación no encontrada"));
        notification.setRead(true);
        repo.save(notification);
    }

    @Transactional
    public void markAllAsRead(String identifier) {
        User user = resolveUser(identifier);
        repo.markAllAsRead(user.getId());
    }

    @Transactional
    public void clearAll(String identifier) {
        User user = resolveUser(identifier);
        repo.deleteByUserId(user.getId());
    }

    /**
     * Punto de entrada interno usado por otros servicios (likes, comentarios, seguidores,
     * solicitudes de amistad) para generar una notificación real. No se expone como
     * endpoint público: cualquier usuario podría inyectar notificaciones falsas a otros
     * si esto fuera un POST abierto.
     */
    /**
     * Igual que notify(), pero si ya hay una notificación sin leer del mismo tipo y relatedId
     * la actualiza en lugar de crear otra (ej. varios mensajes seguidos de la misma conversación).
     */
    public void notifyGrouped(Integer recipientUserId, String type, String content, String sourceUsername, String sourceAvatarUrl, Integer relatedId) {
        if (recipientUserId == null || !isEnabledFor(recipientUserId, type)) return;
        Notification existing = repo.findFirstByUserIdAndTypeAndRelatedIdAndReadFalse(recipientUserId, type, relatedId).orElse(null);
        if (existing == null) {
            notify(recipientUserId, type, content, sourceUsername, sourceAvatarUrl, relatedId);
            return;
        }
        existing.setContent(content);
        existing.setSourceUsername(sourceUsername);
        existing.setSourceAvatarUrl(sourceAvatarUrl);
        existing.setCreatedAt(LocalDateTime.now());
        repo.save(existing);
    }

    public void notify(Integer recipientUserId, String type, String content, String sourceUsername, String sourceAvatarUrl, Integer relatedId) {
        if (recipientUserId == null || !isEnabledFor(recipientUserId, type)) return;

        repo.save(Notification.builder()
                .userId(recipientUserId)
                .type(type)
                .content(content)
                .sourceUsername(sourceUsername)
                .sourceAvatarUrl(sourceAvatarUrl)
                .relatedId(relatedId)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
