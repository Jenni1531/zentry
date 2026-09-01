package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.SessionTrackingRequest;
import zentry.back.api.analytics.dtos.SessionTrackingResponse;
import zentry.back.api.analytics.models.SessionTracking;
import zentry.back.api.analytics.repositories.SessionTrackingRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class SessionTrackingService {

    private final SessionTrackingRepository repo;

    public SessionTrackingService(SessionTrackingRepository repo) {
        this.repo = repo;
    }

    public Page<SessionTrackingResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SessionTrackingResponse getById(Integer id) {
        SessionTracking entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SessionTracking not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SessionTrackingResponse create(SessionTrackingRequest request) {
        SessionTracking entity = SessionTracking.builder()
                .userId(request.getUserId())
                .sessionStart(request.getSessionStart())
                .sessionEnd(request.getSessionEnd())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SessionTrackingResponse update(Integer id, SessionTrackingRequest request) {
        SessionTracking entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SessionTracking not found"));
        entity.setUserId(request.getUserId());
        entity.setSessionStart(request.getSessionStart());
        entity.setSessionEnd(request.getSessionEnd());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        SessionTracking entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SessionTracking not found"));
        repo.delete(entity);
    }
}
