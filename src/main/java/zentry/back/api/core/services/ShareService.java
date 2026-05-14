package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.ShareRequest;
import zentry.back.api.core.dtos.ShareResponse;
import zentry.back.api.core.models.Share;
import zentry.back.api.core.repositories.ShareRepository;
import zentry.back.api.global.mappers;

@Service
public class ShareService {

    private final ShareRepository repo;

    public ShareService(ShareRepository repo) {
        this.repo = repo;
    }

    public Page<ShareResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public ShareResponse getById(Integer id) {
        Share entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share not found"));
        return mappers.toResponse(entity);
    }

    public ShareResponse create(ShareRequest request) {
        Share entity = Share.builder()
                .postId(request.getPostId())
                .userId(request.getUserId())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public ShareResponse update(Integer id, ShareRequest request) {
        Share entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share not found"));
        entity.setPostId(request.getPostId());
        entity.setUserId(request.getUserId());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Share entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Share not found"));
        repo.delete(entity);
    }
}
