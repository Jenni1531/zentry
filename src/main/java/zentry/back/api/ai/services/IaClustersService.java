package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaClustersRequest;
import zentry.back.api.ai.dtos.IaClustersResponse;
import zentry.back.api.ai.models.IaClusters;
import zentry.back.api.ai.repositories.IaClustersRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class IaClustersService {

    private final IaClustersRepository repo;

    public IaClustersService(IaClustersRepository repo) {
        this.repo = repo;
    }

    public Page<IaClustersResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaClustersResponse getById(UUID id) {
        IaClusters entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cluster not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaClustersResponse create(IaClustersRequest request) {
        if (request.getDescripcion() != null && repo.existsByDescripcion(request.getDescripcion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cluster already exists");
        }
        IaClusters entity = IaClusters.builder()
                .descripcion(request.getDescripcion())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaClustersResponse update(UUID id, IaClustersRequest request) {
        IaClusters entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cluster not found"));
        entity.setDescripcion(request.getDescripcion());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        IaClusters entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cluster not found"));
        repo.delete(entity);
    }
}
