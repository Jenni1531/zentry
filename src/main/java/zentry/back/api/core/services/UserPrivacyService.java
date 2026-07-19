package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.UserPrivacyRequest;
import zentry.back.api.core.dtos.UserPrivacyResponse;
import zentry.back.api.core.models.UserPrivacy;
import zentry.back.api.core.repositories.UserPrivacyRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class UserPrivacyService {

    private final UserPrivacyRepository repo;

    public UserPrivacyService(UserPrivacyRepository repo) {
        this.repo = repo;
    }

    public Page<UserPrivacyResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public UserPrivacyResponse getById(Integer userId) {
        UserPrivacy entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserPrivacy not found"));
        return mappers.toResponse(entity);
    }

    public UserPrivacyResponse create(UserPrivacyRequest request) {
        if (repo.existsById(request.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserPrivacy already exists for this user");
        }
        UserPrivacy entity = UserPrivacy.builder()
                .userId(request.getUserId())
                .nivel(request.getNivel())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public UserPrivacyResponse update(Integer userId, UserPrivacyRequest request) {
        UserPrivacy entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserPrivacy not found"));
        entity.setNivel(request.getNivel());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer userId) {
        UserPrivacy entity = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserPrivacy not found"));
        repo.delete(entity);
    }
}
