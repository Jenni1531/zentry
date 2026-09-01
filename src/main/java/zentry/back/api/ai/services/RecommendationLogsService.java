package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.RecommendationLogsRequest;
import zentry.back.api.ai.dtos.RecommendationLogsResponse;
import zentry.back.api.ai.models.RecommendationLogs;
import zentry.back.api.ai.repositories.RecommendationLogsRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class RecommendationLogsService {

    private final RecommendationLogsRepository repo;

    public RecommendationLogsService(RecommendationLogsRepository repo) {
        this.repo = repo;
    }

    public Page<RecommendationLogsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RecommendationLogsResponse getById(UUID id) {
        RecommendationLogs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RecommendationLog not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RecommendationLogsResponse create(RecommendationLogsRequest request) {
        RecommendationLogs entity = RecommendationLogs.builder()
                .recommendationId(request.getRecommendationId())
                .timestamp(request.getTimestamp())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RecommendationLogsResponse update(UUID id, RecommendationLogsRequest request) {
        RecommendationLogs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RecommendationLog not found"));
        entity.setRecommendationId(request.getRecommendationId());
        entity.setTimestamp(request.getTimestamp());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        RecommendationLogs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RecommendationLog not found"));
        repo.delete(entity);
    }
}
