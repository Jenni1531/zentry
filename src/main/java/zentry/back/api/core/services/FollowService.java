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
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class FollowService {

    private final FollowRepository repo;

    public FollowService(FollowRepository repo) {
        this.repo = repo;
    }

    public Page<FollowResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public FollowResponse getById(Integer follower, Integer following) {
        FollowId id = new FollowId(follower, following);
        Follow entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
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
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer follower, Integer following) {
        FollowId id = new FollowId(follower, following);
        Follow entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow not found"));
        repo.delete(entity);
    }
}
