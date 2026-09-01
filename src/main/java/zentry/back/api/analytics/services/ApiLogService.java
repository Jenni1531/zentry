package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.ApiLogRequest;
import zentry.back.api.analytics.dtos.ApiLogResponse;
import zentry.back.api.analytics.models.ApiLog;
import zentry.back.api.analytics.repositories.ApiLogRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class ApiLogService {

    private final ApiLogRepository repo;

    public ApiLogService(ApiLogRepository repo) {
        this.repo = repo;
    }

    public Page<ApiLogResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ApiLogResponse getById(Integer id) {
        ApiLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ApiLog not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ApiLogResponse create(ApiLogRequest request) {
        ApiLog entity = ApiLog.builder()
                .endpoint(request.getEndpoint())
                .method(request.getMethod())
                .responseTime(request.getResponseTime())
                .statusCode(request.getStatusCode())
                .userId(request.getUserId())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ApiLogResponse update(Integer id, ApiLogRequest request) {
        ApiLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ApiLog not found"));
        entity.setEndpoint(request.getEndpoint());
        entity.setMethod(request.getMethod());
        entity.setResponseTime(request.getResponseTime());
        entity.setStatusCode(request.getStatusCode());
        entity.setUserId(request.getUserId());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        ApiLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ApiLog not found"));
        repo.delete(entity);
    }
}
