package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.UserSessionRequest;
import zentry.back.api.core.dtos.UserSessionResponse;
import zentry.back.api.core.models.UserSession;
import zentry.back.api.core.repositories.UserSessionRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class UserSessionService {

    private final UserSessionRepository repo;

    public UserSessionService(UserSessionRepository repo) {
        this.repo = repo;
    }

    public Page<UserSessionResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserSessionResponse getById(Integer id) {
        UserSession entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserSession not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserSessionResponse create(UserSessionRequest request) {
        UserSession entity = UserSession.builder()
                .userId(request.getUserId())
                .token(request.getToken())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserSessionResponse update(Integer id, UserSessionRequest request) {
        UserSession entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserSession not found"));
        entity.setUserId(request.getUserId());
        entity.setToken(request.getToken());
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        UserSession entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserSession not found"));
        repo.delete(entity);
    }
}
