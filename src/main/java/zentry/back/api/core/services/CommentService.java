package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.CommentRequest;
import zentry.back.api.core.dtos.CommentResponse;
import zentry.back.api.core.models.Comment;
import zentry.back.api.core.repositories.CommentRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class CommentService {

    private final CommentRepository repo;

    public CommentService(CommentRepository repo) {
        this.repo = repo;
    }

    public Page<CommentResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public CommentResponse getById(Integer id) {
        Comment entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        return mappers.toResponse(entity);
    }

    public CommentResponse create(CommentRequest request) {
        Comment entity = Comment.builder()
                .postId(request.getPostId())
                .userId(request.getUserId())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public CommentResponse update(Integer id, CommentRequest request) {
        Comment entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        entity.setPostId(request.getPostId());
        entity.setUserId(request.getUserId());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Comment entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        repo.delete(entity);
    }
}
