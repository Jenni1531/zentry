package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.AbTestResultRequest;
import zentry.back.api.analytics.dtos.AbTestResultResponse;
import zentry.back.api.analytics.models.AbTestResult;
import zentry.back.api.analytics.repositories.AbTestResultRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class AbTestResultService {

    private final AbTestResultRepository repo;

    public AbTestResultService(AbTestResultRepository repo) {
        this.repo = repo;
    }

    public Page<AbTestResultResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AbTestResultResponse getById(Integer id) {
        AbTestResult entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AbTestResult not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AbTestResultResponse create(AbTestResultRequest request) {
        AbTestResult entity = AbTestResult.builder()
                .testId(request.getTestId())
                .variant(request.getVariant())
                .result(request.getResult())
                .userId(request.getUserId())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AbTestResultResponse update(Integer id, AbTestResultRequest request) {
        AbTestResult entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AbTestResult not found"));
        entity.setTestId(request.getTestId());
        entity.setVariant(request.getVariant());
        entity.setResult(request.getResult());
        entity.setUserId(request.getUserId());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        AbTestResult entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AbTestResult not found"));
        repo.delete(entity);
    }
}
