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
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class CommissionJobsService {

    private final CommissionJobsRepository repo;

    public CommissionJobsService(CommissionJobsRepository repo) {
        this.repo = repo;
    }

    public Page<CommissionJobsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommissionJobsResponse getById(UUID id) {
        CommissionJobs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommissionJob not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommissionJobsResponse create(CommissionJobsRequest request) {
        CommissionJobs entity = CommissionJobs.builder()
                .userId(request.getUserId())
                .descripcion(request.getDescripcion())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommissionJobsResponse update(UUID id, CommissionJobsRequest request) {
        CommissionJobs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommissionJob not found"));
        entity.setUserId(request.getUserId());
        entity.setDescripcion(request.getDescripcion());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        CommissionJobs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommissionJob not found"));
        repo.delete(entity);
    }
}
