package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaPromptsRequest;
import zentry.back.api.ai.dtos.IaPromptsResponse;
import zentry.back.api.ai.models.IaPrompts;
import zentry.back.api.ai.repositories.IaPromptsRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class IaPromptsService {

    private final IaPromptsRepository repo;

    public IaPromptsService(IaPromptsRepository repo) {
        this.repo = repo;
    }

    public Page<IaPromptsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaPromptsResponse getById(UUID id) {
        IaPrompts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prompt not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaPromptsResponse create(IaPromptsRequest request) {
        IaPrompts entity = IaPrompts.builder()
                .userId(request.getUserId())
                .prompt(request.getPrompt())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaPromptsResponse update(UUID id, IaPromptsRequest request) {
        IaPrompts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prompt not found"));
        entity.setUserId(request.getUserId());
        entity.setPrompt(request.getPrompt());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        IaPrompts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prompt not found"));
        repo.delete(entity);
    }
}
