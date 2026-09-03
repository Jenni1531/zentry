package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.FollowRequest;
import zentry.back.api.core.dtos.FollowResponse;
import zentry.back.api.core.models.Follow;
import zentry.back.api.core.models.Follow.FollowId;
import zentry.back.api.core.repositories.FollowRepository;
import zentry.back.api.core.mappers.CoreMappers;

@Service
@SuppressWarnings("null")
public class FollowService {

    private final FollowRepository repo;

    public FollowService(FollowRepository repo) {
        this.repo = repo;
    }

    public Page<FollowResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(CoreMappers::toResponse);
    }

    public FollowResponse getById(Integer follower, Integer following) {
        FollowId id = new FollowId(follower, following);
        Follow entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow not found"));
        return CoreMappers.toResponse(entity);
    }

    public FollowResponse create(FollowRequest request) {
        FollowId id = new FollowId(request.getFollower(), request.getFollowing());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Follow already exists");
        }
        Follow entity = Follow.builder()
                .follower(request.getFollower())
                .following(request.getFollowing())
                .build();
        return CoreMappers.toResponse(repo.save(entity));
    }

    public void delete(Integer follower, Integer following) {
        FollowId id = new FollowId(follower, following);
        Follow entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow not found"));
        repo.delete(entity);
    }
}
