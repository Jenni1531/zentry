package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.PostTagRequest;
import zentry.back.api.core.dtos.PostTagResponse;
import zentry.back.api.core.models.PostTag;
import zentry.back.api.core.models.PostTag.PostTagId;
import zentry.back.api.core.repositories.PostTagRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class PostTagService {

    private final PostTagRepository repo;

    public PostTagService(PostTagRepository repo) {
        this.repo = repo;
    }

    public Page<PostTagResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PostTagResponse getById(Integer postId, Integer tagId) {
        PostTagId id = new PostTagId(postId, tagId);
        PostTag entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PostTag not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PostTagResponse create(PostTagRequest request) {
        PostTagId id = new PostTagId(request.getPostId(), request.getTagId());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PostTag already exists");
        }
        PostTag entity = PostTag.builder()
                .postId(request.getPostId())
                .tagId(request.getTagId())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer postId, Integer tagId) {
        PostTagId id = new PostTagId(postId, tagId);
        PostTag entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PostTag not found"));
        repo.delete(entity);
    }
}
