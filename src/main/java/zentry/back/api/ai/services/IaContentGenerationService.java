package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaContentGenerationRequest;
import zentry.back.api.ai.dtos.IaContentGenerationResponse;
import zentry.back.api.ai.models.IaContentGeneration;
import zentry.back.api.ai.repositories.IaContentGenerationRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class IaContentGenerationService {

    private final IaContentGenerationRepository repo;

    public IaContentGenerationService(IaContentGenerationRepository repo) {
        this.repo = repo;
    }

    public Page<IaContentGenerationResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaContentGenerationResponse getById(UUID id) {
        IaContentGeneration entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentGeneration not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaContentGenerationResponse create(IaContentGenerationRequest request) {
        IaContentGeneration entity = IaContentGeneration.builder()
                .promptId(request.getPromptId())
                .resultado(request.getResultado())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaContentGenerationResponse update(UUID id, IaContentGenerationRequest request) {
        IaContentGeneration entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentGeneration not found"));
        entity.setPromptId(request.getPromptId());
        entity.setResultado(request.getResultado());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        IaContentGeneration entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentGeneration not found"));
        repo.delete(entity);
    }
}
