package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.TagEmbeddingsRequest;
import zentry.back.api.ai.dtos.TagEmbeddingsResponse;
import zentry.back.api.ai.models.TagEmbeddings;
import zentry.back.api.ai.repositories.TagEmbeddingsRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class TagEmbeddingsService {

    private final TagEmbeddingsRepository repo;

    public TagEmbeddingsService(TagEmbeddingsRepository repo) {
        this.repo = repo;
    }

    public Page<TagEmbeddingsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public TagEmbeddingsResponse getById(UUID id) {
        TagEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TagEmbedding not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public TagEmbeddingsResponse create(TagEmbeddingsRequest request) {
        TagEmbeddings entity = TagEmbeddings.builder()
                .tagId(request.getTagId())
                .vector(request.getVector())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public TagEmbeddingsResponse update(UUID id, TagEmbeddingsRequest request) {
        TagEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TagEmbedding not found"));
        entity.setTagId(request.getTagId());
        entity.setVector(request.getVector());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        TagEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TagEmbedding not found"));
        repo.delete(entity);
    }
}
