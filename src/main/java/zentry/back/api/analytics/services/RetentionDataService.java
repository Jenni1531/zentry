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
import zentry.back.api.global.mappers;

@Service
public class RetentionDataService {

    private final RetentionDataRepository repo;

    public RetentionDataService(RetentionDataRepository repo) {
        this.repo = repo;
    }

    public Page<RetentionDataResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public RetentionDataResponse getById(Integer id) {
        RetentionData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RetentionData not found"));
        return mappers.toResponse(entity);
    }

    public RetentionDataResponse create(RetentionDataRequest request) {
        RetentionData entity = RetentionData.builder()
                .cohort(request.getCohort())
                .retentionRate(request.getRetentionRate())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public RetentionDataResponse update(Integer id, RetentionDataRequest request) {
        RetentionData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RetentionData not found"));
        entity.setCohort(request.getCohort());
        entity.setRetentionRate(request.getRetentionRate());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        RetentionData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RetentionData not found"));
        repo.delete(entity);
    }
}
