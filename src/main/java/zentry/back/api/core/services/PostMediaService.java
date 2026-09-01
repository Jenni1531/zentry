package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.PostMediaRequest;
import zentry.back.api.core.dtos.PostMediaResponse;
import zentry.back.api.core.models.PostMedia;
import zentry.back.api.core.repositories.PostMediaRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class PostMediaService {

    private final PostMediaRepository repo;

    public PostMediaService(PostMediaRepository repo) {
        this.repo = repo;
    }

    public Page<PostMediaResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PostMediaResponse getById(Integer id) {
        PostMedia entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PostMedia not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PostMediaResponse create(PostMediaRequest request) {
        PostMedia entity = PostMedia.builder()
                .postId(request.getPostId())
                .url(request.getUrl())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PostMediaResponse update(Integer id, PostMediaRequest request) {
        PostMedia entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PostMedia not found"));
        entity.setPostId(request.getPostId());
        entity.setUrl(request.getUrl());
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        PostMedia entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PostMedia not found"));
        repo.delete(entity);
    }
}
