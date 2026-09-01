package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.UserEmbeddingsRequest;
import zentry.back.api.ai.dtos.UserEmbeddingsResponse;
import zentry.back.api.ai.models.UserEmbeddings;
import zentry.back.api.ai.repositories.UserEmbeddingsRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class UserEmbeddingsService {

    private final UserEmbeddingsRepository repo;

    public UserEmbeddingsService(UserEmbeddingsRepository repo) {
        this.repo = repo;
    }

    public Page<UserEmbeddingsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserEmbeddingsResponse getById(Integer userId) {
        UserEmbeddings entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserEmbedding not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserEmbeddingsResponse create(UserEmbeddingsRequest request) {
        if (repo.existsByUserId(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Embedding already exists for this user");
        }
        UserEmbeddings entity = UserEmbeddings.builder()
                .userId(request.getUserId())
                .vector(request.getVector())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public UserEmbeddingsResponse update(Integer userId, UserEmbeddingsRequest request) {
        UserEmbeddings entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserEmbedding not found"));
        entity.setVector(request.getVector());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer userId) {
        UserEmbeddings entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserEmbedding not found"));
        repo.delete(entity);
    }
}
