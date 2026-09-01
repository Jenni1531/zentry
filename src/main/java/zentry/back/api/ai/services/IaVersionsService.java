package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaVersionsRequest;
import zentry.back.api.ai.dtos.IaVersionsResponse;
import zentry.back.api.ai.models.IaVersions;
import zentry.back.api.ai.repositories.IaVersionsRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class IaVersionsService {

    private final IaVersionsRepository repo;

    public IaVersionsService(IaVersionsRepository repo) {
        this.repo = repo;
    }

    public Page<IaVersionsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaVersionsResponse getById(UUID id) {
        IaVersions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaVersionsResponse create(IaVersionsRequest request) {
        if (repo.existsByModelIdAndVersion(request.getModelId(), request.getVersion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Version already exists for this model");
        }
        IaVersions entity = IaVersions.builder()
                .modelId(request.getModelId())
                .version(request.getVersion())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaVersionsResponse update(UUID id, IaVersionsRequest request) {
        IaVersions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));
        entity.setModelId(request.getModelId());
        entity.setVersion(request.getVersion());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        IaVersions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));
        repo.delete(entity);
    }
}
