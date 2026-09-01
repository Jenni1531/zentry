package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.CommentReactionRequest;
import zentry.back.api.core.dtos.CommentReactionResponse;
import zentry.back.api.core.models.CommentReaction;
import zentry.back.api.core.repositories.CommentReactionRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class CommentReactionService {

    private final CommentReactionRepository repo;

    public CommentReactionService(CommentReactionRepository repo) {
        this.repo = repo;
    }

    public Page<CommentReactionResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommentReactionResponse getById(Integer id) {
        CommentReaction entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommentReaction not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommentReactionResponse create(CommentReactionRequest request) {
        CommentReaction entity = CommentReaction.builder()
                .commentId(request.getCommentId())
                .userId(request.getUserId())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommentReactionResponse update(Integer id, CommentReactionRequest request) {
        CommentReaction entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommentReaction not found"));
        entity.setCommentId(request.getCommentId());
        entity.setUserId(request.getUserId());
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        CommentReaction entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommentReaction not found"));
        repo.delete(entity);
    }
}
