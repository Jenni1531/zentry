package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.ReactionRequest;
import zentry.back.api.core.dtos.ReactionResponse;
import zentry.back.api.core.models.Reaction;
import zentry.back.api.core.repositories.ReactionRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class ReactionService {

    private final ReactionRepository repo;

    public ReactionService(ReactionRepository repo) {
        this.repo = repo;
    }

    public Page<ReactionResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public ReactionResponse getById(Integer id) {
        Reaction entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found"));
        return mappers.toResponse(entity);
    }

    public ReactionResponse create(ReactionRequest request) {
        Reaction entity = Reaction.builder()
                .postId(request.getPostId())
                .userId(request.getUserId())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public ReactionResponse update(Integer id, ReactionRequest request) {
        Reaction entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found"));
        entity.setPostId(request.getPostId());
        entity.setUserId(request.getUserId());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Reaction entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found"));
        repo.delete(entity);
    }
}
