package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.CommissionJobsRequest;
import zentry.back.api.business.dtos.CommissionJobsResponse;
import zentry.back.api.business.models.CommissionJobs;
import zentry.back.api.business.repositories.CommissionJobsRepository;
import zentry.back.api.global.mappers;

import java.util.UUID;

@Service
public class CommissionJobsService {

    private final CommissionJobsRepository repo;

    public CommissionJobsService(CommissionJobsRepository repo) {
        this.repo = repo;
    }

    public Page<CommissionJobsResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public CommissionJobsResponse getById(UUID id) {
        CommissionJobs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommissionJob not found"));
        return mappers.toResponse(entity);
    }

    public CommissionJobsResponse create(CommissionJobsRequest request) {
        CommissionJobs entity = CommissionJobs.builder()
                .userId(request.getUserId())
                .descripcion(request.getDescripcion())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public CommissionJobsResponse update(UUID id, CommissionJobsRequest request) {
        CommissionJobs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommissionJob not found"));
        entity.setUserId(request.getUserId());
        entity.setDescripcion(request.getDescripcion());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(UUID id) {
        CommissionJobs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommissionJob not found"));
        repo.delete(entity);
    }
}
