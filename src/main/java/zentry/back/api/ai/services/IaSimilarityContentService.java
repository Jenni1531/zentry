package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaSimilarityContentRequest;
import zentry.back.api.ai.dtos.IaSimilarityContentResponse;
import zentry.back.api.ai.models.IaSimilarityContent;
import zentry.back.api.ai.repositories.IaSimilarityContentRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class IaSimilarityContentService {

    private final IaSimilarityContentRepository repo;

    public IaSimilarityContentService(IaSimilarityContentRepository repo) {
        this.repo = repo;
    }

    public Page<IaSimilarityContentResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaSimilarityContentResponse getById(UUID id) {
        IaSimilarityContent entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SimilarityContent not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaSimilarityContentResponse create(IaSimilarityContentRequest request) {
        if (repo.existsByPostId1AndPostId2(request.getPostId1(), request.getPostId2())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Similarity record already exists for these posts");
        }
        IaSimilarityContent entity = IaSimilarityContent.builder()
                .postId1(request.getPostId1())
                .postId2(request.getPostId2())
                .score(request.getScore())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaSimilarityContentResponse update(UUID id, IaSimilarityContentRequest request) {
        IaSimilarityContent entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SimilarityContent not found"));
        entity.setPostId1(request.getPostId1());
        entity.setPostId2(request.getPostId2());
        entity.setScore(request.getScore());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        IaSimilarityContent entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SimilarityContent not found"));
        repo.delete(entity);
    }
}
