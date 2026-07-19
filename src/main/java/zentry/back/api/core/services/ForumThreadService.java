package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.ForumThreadRequest;
import zentry.back.api.core.dtos.ForumThreadResponse;
import zentry.back.api.core.models.ForumThread;
import zentry.back.api.core.repositories.ForumThreadRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class ForumThreadService {

    private final ForumThreadRepository repo;

    public ForumThreadService(ForumThreadRepository repo) {
        this.repo = repo;
    }

    public Page<ForumThreadResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public ForumThreadResponse getById(Integer id) {
        ForumThread entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ForumThread not found"));
        return mappers.toResponse(entity);
    }

    public ForumThreadResponse create(ForumThreadRequest request) {
        ForumThread entity = ForumThread.builder()
                .communityId(request.getCommunityId())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public ForumThreadResponse update(Integer id, ForumThreadRequest request) {
        ForumThread entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ForumThread not found"));
        entity.setCommunityId(request.getCommunityId());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        ForumThread entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ForumThread not found"));
        repo.delete(entity);
    }
}
