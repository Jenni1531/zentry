package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.CommunityEmbeddingsRequest;
import zentry.back.api.ai.dtos.CommunityEmbeddingsResponse;
import zentry.back.api.ai.models.CommunityEmbeddings;
import zentry.back.api.ai.repositories.CommunityEmbeddingsRepository;
import zentry.back.api.global.mappers;

import java.util.UUID;

@Service
public class CommunityEmbeddingsService {

    private final CommunityEmbeddingsRepository repo;

    public CommunityEmbeddingsService(CommunityEmbeddingsRepository repo) {
        this.repo = repo;
    }

    public Page<CommunityEmbeddingsResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public CommunityEmbeddingsResponse getById(UUID id) {
        CommunityEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommunityEmbedding not found"));
        return mappers.toResponse(entity);
    }

    public CommunityEmbeddingsResponse create(CommunityEmbeddingsRequest request) {
        CommunityEmbeddings entity = CommunityEmbeddings.builder()
                .communityId(request.getCommunityId())
                .vector(request.getVector())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public CommunityEmbeddingsResponse update(UUID id, CommunityEmbeddingsRequest request) {
        CommunityEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommunityEmbedding not found"));
        entity.setCommunityId(request.getCommunityId());
        entity.setVector(request.getVector());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(UUID id) {
        CommunityEmbeddings entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommunityEmbedding not found"));
        repo.delete(entity);
    }
}
