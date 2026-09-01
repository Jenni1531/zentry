package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.UserBehaviorRequest;
import zentry.back.api.analytics.dtos.UserBehaviorResponse;
import zentry.back.api.analytics.models.UserBehavior;
import zentry.back.api.analytics.repositories.UserBehaviorRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class UserBehaviorService {

    private final UserBehaviorRepository repo;

    public UserBehaviorService(UserBehaviorRepository repo) {
        this.repo = repo;
    }

    public Page<UserBehaviorResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserBehaviorResponse getById(Integer id) {
        UserBehavior entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserBehavior not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserBehaviorResponse create(UserBehaviorRequest request) {
        UserBehavior entity = UserBehavior.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .metadata(request.getMetadata())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserBehaviorResponse update(Integer id, UserBehaviorRequest request) {
        UserBehavior entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserBehavior not found"));
        entity.setUserId(request.getUserId());
        entity.setType(request.getType());
        entity.setMetadata(request.getMetadata());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        UserBehavior entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserBehavior not found"));
        repo.delete(entity);
    }
}
