package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.models.Post;
import zentry.back.api.core.repositories.PostRepository;
import zentry.back.api.global.mappers;

@Service
public class PostService {

    private final PostRepository repo;

    public PostService(PostRepository repo) {
        this.repo = repo;
    }

    public Page<PostResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public PostResponse getById(Integer id) {
        Post entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return mappers.toResponse(entity);
    }

    public PostResponse create(PostRequest request) {
        Post entity = Post.builder()
                .userId(request.getUserId())
                .contenido(request.getContenido())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public PostResponse update(Integer id, PostRequest request) {
        Post entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        entity.setUserId(request.getUserId());
        entity.setContenido(request.getContenido());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Post entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        repo.delete(entity);
    }
}
