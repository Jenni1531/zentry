package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.UserRequest;
import zentry.back.api.core.dtos.UserResponse;
import zentry.back.api.core.models.User;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.global.mappers;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Page<UserResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public UserResponse getById(Integer id) {
        User entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mappers.toResponse(entity);
    }

    public UserResponse create(UserRequest request) {
        User entity = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public UserResponse update(Integer id, UserRequest request) {
        User entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        User entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        repo.delete(entity);
    }
}
