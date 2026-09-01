package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.PerformanceLogRequest;
import zentry.back.api.analytics.dtos.PerformanceLogResponse;
import zentry.back.api.analytics.models.PerformanceLog;
import zentry.back.api.analytics.repositories.PerformanceLogRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class PerformanceLogService {

    private final PerformanceLogRepository repo;

    public PerformanceLogService(PerformanceLogRepository repo) {
        this.repo = repo;
    }

    public Page<PerformanceLogResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PerformanceLogResponse getById(Integer id) {
        PerformanceLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PerformanceLog not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PerformanceLogResponse create(PerformanceLogRequest request) {
        PerformanceLog entity = PerformanceLog.builder()
                .metric(request.getMetric())
                .value(request.getValue())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PerformanceLogResponse update(Integer id, PerformanceLogRequest request) {
        PerformanceLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PerformanceLog not found"));
        entity.setMetric(request.getMetric());
        entity.setValue(request.getValue());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        PerformanceLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PerformanceLog not found"));
        repo.delete(entity);
    }
}
