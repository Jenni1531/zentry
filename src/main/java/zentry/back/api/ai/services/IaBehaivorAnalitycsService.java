package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaBehaivorAnalitycsRequest;
import zentry.back.api.ai.dtos.IaBehaivorAnalitycsResponse;
import zentry.back.api.ai.models.IaBehaivorAnalitycs;
import zentry.back.api.ai.repositories.IaBehaivorAnalitycsRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class IaBehaivorAnalitycsService {

    private final IaBehaivorAnalitycsRepository repo;

    public IaBehaivorAnalitycsService(IaBehaivorAnalitycsRepository repo) {
        this.repo = repo;
    }

    public Page<IaBehaivorAnalitycsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaBehaivorAnalitycsResponse getById(UUID id) {
        IaBehaivorAnalitycs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BehaviorAnalytic not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaBehaivorAnalitycsResponse create(IaBehaivorAnalitycsRequest request) {
        IaBehaivorAnalitycs entity = IaBehaivorAnalitycs.builder()
                .userId(request.getUserId())
                .score(request.getScore())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaBehaivorAnalitycsResponse update(UUID id, IaBehaivorAnalitycsRequest request) {
        IaBehaivorAnalitycs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BehaviorAnalytic not found"));
        entity.setUserId(request.getUserId());
        entity.setScore(request.getScore());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        IaBehaivorAnalitycs entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BehaviorAnalytic not found"));
        repo.delete(entity);
    }
}
