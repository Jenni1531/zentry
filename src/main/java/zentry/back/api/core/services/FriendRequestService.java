package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.FriendRequestRequest;
import zentry.back.api.core.dtos.FriendRequestResponse;
import zentry.back.api.core.models.FriendRequest;
import zentry.back.api.core.repositories.FriendRequestRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class FriendRequestService {

    private final FriendRequestRepository repo;

    public FriendRequestService(FriendRequestRepository repo) {
        this.repo = repo;
    }

    public Page<FriendRequestResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public FriendRequestResponse getById(Integer id) {
        FriendRequest entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FriendRequest not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public FriendRequestResponse create(FriendRequestRequest request) {
        FriendRequest entity = FriendRequest.builder()
                .user1(request.getUser1())
                .user2(request.getUser2())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public FriendRequestResponse update(Integer id, FriendRequestRequest request) {
        FriendRequest entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FriendRequest not found"));
        entity.setUser1(request.getUser1());
        entity.setUser2(request.getUser2());
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        FriendRequest entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FriendRequest not found"));
        repo.delete(entity);
    }
}
