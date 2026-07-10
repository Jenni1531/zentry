package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.EngagementMetricRequest;
import zentry.back.api.analytics.dtos.EngagementMetricResponse;
import zentry.back.api.analytics.models.EngagementMetric;
import zentry.back.api.analytics.repositories.EngagementMetricRepository;
import zentry.back.api.global.mappers;

@Service
public class EngagementMetricService {

    private final EngagementMetricRepository repo;

    public EngagementMetricService(EngagementMetricRepository repo) {
        this.repo = repo;
    }

    public Page<EngagementMetricResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public EngagementMetricResponse getById(Integer id) {
        EngagementMetric entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EngagementMetric not found"));
        return mappers.toResponse(entity);
    }

    public EngagementMetricResponse create(EngagementMetricRequest request) {
        EngagementMetric entity = EngagementMetric.builder()
                .userId(request.getUserId())
                .score(request.getScore())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public EngagementMetricResponse update(Integer id, EngagementMetricRequest request) {
        EngagementMetric entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EngagementMetric not found"));
        entity.setUserId(request.getUserId());
        entity.setScore(request.getScore());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        EngagementMetric entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EngagementMetric not found"));
        repo.delete(entity);
    }
}
