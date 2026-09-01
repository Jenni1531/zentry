package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.ErrorTrackingRequest;
import zentry.back.api.analytics.dtos.ErrorTrackingResponse;
import zentry.back.api.analytics.models.ErrorTracking;
import zentry.back.api.analytics.repositories.ErrorTrackingRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class ErrorTrackingService {

    private final ErrorTrackingRepository repo;

    public ErrorTrackingService(ErrorTrackingRepository repo) {
        this.repo = repo;
    }

    public Page<ErrorTrackingResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ErrorTrackingResponse getById(Integer id) {
        ErrorTracking entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ErrorTracking not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ErrorTrackingResponse create(ErrorTrackingRequest request) {
        ErrorTracking entity = ErrorTracking.builder()
                .error(request.getError())
                .stackTrace(request.getStackTrace())
                .userId(request.getUserId())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ErrorTrackingResponse update(Integer id, ErrorTrackingRequest request) {
        ErrorTracking entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ErrorTracking not found"));
        entity.setError(request.getError());
        entity.setStackTrace(request.getStackTrace());
        entity.setUserId(request.getUserId());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        ErrorTracking entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ErrorTracking not found"));
        repo.delete(entity);
    }
}
