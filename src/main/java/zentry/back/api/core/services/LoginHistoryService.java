package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.LoginHistoryRequest;
import zentry.back.api.core.dtos.LoginHistoryResponse;
import zentry.back.api.core.models.LoginHistory;
import zentry.back.api.core.repositories.LoginHistoryRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class LoginHistoryService {

    private final LoginHistoryRepository repo;

    public LoginHistoryService(LoginHistoryRepository repo) {
        this.repo = repo;
    }

    public Page<LoginHistoryResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public LoginHistoryResponse getById(Integer id) {
        LoginHistory entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LoginHistory not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public LoginHistoryResponse create(LoginHistoryRequest request) {
        LoginHistory entity = LoginHistory.builder()
                .userId(request.getUserId())
                .ip(request.getIp())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public LoginHistoryResponse update(Integer id, LoginHistoryRequest request) {
        LoginHistory entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LoginHistory not found"));
        entity.setUserId(request.getUserId());
        entity.setIp(request.getIp());
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        LoginHistory entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LoginHistory not found"));
        repo.delete(entity);
    }
}
