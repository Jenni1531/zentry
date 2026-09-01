package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaConfigsRequest;
import zentry.back.api.ai.dtos.IaConfigsResponse;
import zentry.back.api.ai.models.IaConfigs;
import zentry.back.api.ai.repositories.IaConfigsRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class IaConfigsService {

    private final IaConfigsRepository repo;

    public IaConfigsService(IaConfigsRepository repo) {
        this.repo = repo;
    }

    public Page<IaConfigsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaConfigsResponse getById(UUID id) {
        IaConfigs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Config not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaConfigsResponse create(IaConfigsRequest request) {
        IaConfigs entity = IaConfigs.builder()
                .modelId(request.getModelId())
                .parametros(request.getParametros())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaConfigsResponse update(UUID id, IaConfigsRequest request) {
        IaConfigs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Config not found"));
        entity.setModelId(request.getModelId());
        entity.setParametros(request.getParametros());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        IaConfigs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Config not found"));
        repo.delete(entity);
    }
}
