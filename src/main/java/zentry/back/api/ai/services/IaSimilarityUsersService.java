package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaSimilarityUsersRequest;
import zentry.back.api.ai.dtos.IaSimilarityUsersResponse;
import zentry.back.api.ai.models.IaSimilarityUsers;
import zentry.back.api.ai.models.IaSimilarityUsersId;
import zentry.back.api.ai.repositories.IaSimilarityUsersRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class IaSimilarityUsersService {

    private final IaSimilarityUsersRepository repo;

    public IaSimilarityUsersService(IaSimilarityUsersRepository repo) {
        this.repo = repo;
    }

    public Page<IaSimilarityUsersResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaSimilarityUsersResponse getById(Integer user1, Integer user2) {
        IaSimilarityUsersId id = new IaSimilarityUsersId(user1, user2);
        IaSimilarityUsers entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SimilarityUsers not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaSimilarityUsersResponse create(IaSimilarityUsersRequest request) {
        if (repo.existsByUser1AndUser2(request.getUser1(), request.getUser2())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Similarity record already exists for these users");
        }
        IaSimilarityUsers entity = IaSimilarityUsers.builder()
                .user1(request.getUser1())
                .user2(request.getUser2())
                .score(request.getScore())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaSimilarityUsersResponse update(Integer user1, Integer user2, IaSimilarityUsersRequest request) {
        IaSimilarityUsersId id = new IaSimilarityUsersId(user1, user2);
        IaSimilarityUsers entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SimilarityUsers not found"));
        entity.setScore(request.getScore());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer user1, Integer user2) {
        IaSimilarityUsersId id = new IaSimilarityUsersId(user1, user2);
        IaSimilarityUsers entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SimilarityUsers not found"));
        repo.delete(entity);
    }
}
