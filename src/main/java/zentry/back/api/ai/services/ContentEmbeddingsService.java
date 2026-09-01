package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.ContentEmbeddingsRequest;
import zentry.back.api.ai.dtos.ContentEmbeddingsResponse;
import zentry.back.api.ai.models.ContentEmbeddings;
import zentry.back.api.ai.repositories.ContentEmbeddingsRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class ContentEmbeddingsService {

    private final ContentEmbeddingsRepository repo;

    public ContentEmbeddingsService(ContentEmbeddingsRepository repo) {
        this.repo = repo;
    }

    public Page<ContentEmbeddingsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ContentEmbeddingsResponse getById(UUID id) {
        ContentEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentEmbedding not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ContentEmbeddingsResponse create(ContentEmbeddingsRequest request) {
        ContentEmbeddings entity = ContentEmbeddings.builder()
                .postId(request.getPostId())
                .vector(request.getVector())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ContentEmbeddingsResponse update(UUID id, ContentEmbeddingsRequest request) {
        ContentEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentEmbedding not found"));
        entity.setPostId(request.getPostId());
        entity.setVector(request.getVector());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        ContentEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentEmbedding not found"));
        repo.delete(entity);
    }
}
