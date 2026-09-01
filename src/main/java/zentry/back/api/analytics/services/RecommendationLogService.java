package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.RecommendationLogRequest;
import zentry.back.api.analytics.dtos.RecommendationLogResponse;
import zentry.back.api.analytics.models.RecommendationLog;
import zentry.back.api.analytics.repositories.RecommendationLogRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class RecommendationLogService {

    private final RecommendationLogRepository repo;

    public RecommendationLogService(RecommendationLogRepository repo) {
        this.repo = repo;
    }

    public Page<RecommendationLogResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RecommendationLogResponse getById(Integer id) {
        RecommendationLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RecommendationLog not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RecommendationLogResponse create(RecommendationLogRequest request) {
        RecommendationLog entity = RecommendationLog.builder()
                .userId(request.getUserId())
                .recommendations(request.getRecommendations())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RecommendationLogResponse update(Integer id, RecommendationLogRequest request) {
        RecommendationLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RecommendationLog not found"));
        entity.setUserId(request.getUserId());
        entity.setRecommendations(request.getRecommendations());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        RecommendationLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RecommendationLog not found"));
        repo.delete(entity);
    }
}
