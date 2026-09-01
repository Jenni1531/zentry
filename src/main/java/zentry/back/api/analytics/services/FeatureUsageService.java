package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.FeatureUsageRequest;
import zentry.back.api.analytics.dtos.FeatureUsageResponse;
import zentry.back.api.analytics.models.FeatureUsage;
import zentry.back.api.analytics.repositories.FeatureUsageRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class FeatureUsageService {

    private final FeatureUsageRepository repo;

    public FeatureUsageService(FeatureUsageRepository repo) {
        this.repo = repo;
    }

    public Page<FeatureUsageResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public FeatureUsageResponse getById(Integer id) {
        FeatureUsage entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FeatureUsage not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public FeatureUsageResponse create(FeatureUsageRequest request) {
        FeatureUsage entity = FeatureUsage.builder()
                .feature(request.getFeature())
                .usageCount(request.getUsageCount())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public FeatureUsageResponse update(Integer id, FeatureUsageRequest request) {
        FeatureUsage entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FeatureUsage not found"));
        entity.setFeature(request.getFeature());
        entity.setUsageCount(request.getUsageCount());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        FeatureUsage entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FeatureUsage not found"));
        repo.delete(entity);
    }
}
