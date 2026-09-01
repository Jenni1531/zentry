package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.UserPreferencesRequest;
import zentry.back.api.core.dtos.UserPreferencesResponse;
import zentry.back.api.core.models.UserPreferences;
import zentry.back.api.core.repositories.UserPreferencesRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class UserPreferencesService {

    private final UserPreferencesRepository repo;

    public UserPreferencesService(UserPreferencesRepository repo) {
        this.repo = repo;
    }

    public Page<UserPreferencesResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserPreferencesResponse getById(Integer userId) {
        UserPreferences entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserPreferences not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserPreferencesResponse create(UserPreferencesRequest request) {
        if (repo.existsById(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserPreferences already exists for this user");
        }
        UserPreferences entity = UserPreferences.builder()
                .userId(request.getUserId())
                .config(request.getConfig())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserPreferencesResponse update(Integer userId, UserPreferencesRequest request) {
        UserPreferences entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserPreferences not found"));
        entity.setConfig(request.getConfig());
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer userId) {
        UserPreferences entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserPreferences not found"));
        repo.delete(entity);
    }
}
