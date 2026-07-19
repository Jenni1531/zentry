package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.ProfileRequest;
import zentry.back.api.core.dtos.ProfileResponse;
import zentry.back.api.core.models.Profile;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class ProfileService {

    private final ProfileRepository repo;

    public ProfileService(ProfileRepository repo) {
        this.repo = repo;
    }

    public Page<ProfileResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public ProfileResponse getById(Integer id) {
        Profile entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        return mappers.toResponse(entity);
    }

    public ProfileResponse create(ProfileRequest request) {
        Profile entity = Profile.builder()
                .userId(request.getUserId())
                .bio(request.getBio())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public ProfileResponse update(Integer id, ProfileRequest request) {
        Profile entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        entity.setUserId(request.getUserId());
        entity.setBio(request.getBio());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Profile entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
        repo.delete(entity);
    }
}
