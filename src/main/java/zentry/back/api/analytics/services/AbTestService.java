package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.AbTestRequest;
import zentry.back.api.analytics.dtos.AbTestResponse;
import zentry.back.api.analytics.models.AbTest;
import zentry.back.api.analytics.repositories.AbTestRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class AbTestService {

    private final AbTestRepository repo;

    public AbTestService(AbTestRepository repo) {
        this.repo = repo;
    }

    public Page<AbTestResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AbTestResponse getById(Integer id) {
        AbTest entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AbTest not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AbTestResponse create(AbTestRequest request) {
        AbTest entity = AbTest.builder()
                .testName(request.getTestName())
                .description(request.getDescription())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AbTestResponse update(Integer id, AbTestRequest request) {
        AbTest entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AbTest not found"));
        entity.setTestName(request.getTestName());
        entity.setDescription(request.getDescription());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        AbTest entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AbTest not found"));
        repo.delete(entity);
    }
}
