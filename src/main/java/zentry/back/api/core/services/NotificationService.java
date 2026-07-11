package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.NotificationRequest;
import zentry.back.api.core.dtos.NotificationResponse;
import zentry.back.api.core.models.Notification;
import zentry.back.api.core.repositories.NotificationRepository;
import zentry.back.api.global.mappers;

@Service("coreNotificationService")
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public Page<NotificationResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public NotificationResponse getById(Integer id) {
        Notification entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        return mappers.toResponse(entity);
    }

    public NotificationResponse create(NotificationRequest request) {
        Notification entity = Notification.builder()
                .userId(request.getUserId())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public NotificationResponse update(Integer id, NotificationRequest request) {
        Notification entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        entity.setUserId(request.getUserId());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Notification entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        repo.delete(entity);
    }
}
