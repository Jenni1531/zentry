package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.RetentionDataRequest;
import zentry.back.api.analytics.dtos.RetentionDataResponse;
import zentry.back.api.analytics.models.RetentionData;
import zentry.back.api.analytics.repositories.RetentionDataRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class RetentionDataService {

    private final RetentionDataRepository repo;

    public RetentionDataService(RetentionDataRepository repo) {
        this.repo = repo;
    }

    public Page<RetentionDataResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RetentionDataResponse getById(Integer id) {
        RetentionData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RetentionData not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RetentionDataResponse create(RetentionDataRequest request) {
        RetentionData entity = RetentionData.builder()
                .cohort(request.getCohort())
                .retentionRate(request.getRetentionRate())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public RetentionDataResponse update(Integer id, RetentionDataRequest request) {
        RetentionData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RetentionData not found"));
        entity.setCohort(request.getCohort());
        entity.setRetentionRate(request.getRetentionRate());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        RetentionData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RetentionData not found"));
        repo.delete(entity);
    }
}
