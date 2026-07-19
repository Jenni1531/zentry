package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.CommunityRequest;
import zentry.back.api.core.dtos.CommunityResponse;
import zentry.back.api.core.models.Community;
import zentry.back.api.core.repositories.CommunityRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class CommunityService {

    private final CommunityRepository repo;

    public CommunityService(CommunityRepository repo) {
        this.repo = repo;
    }

    public Page<CommunityResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public CommunityResponse getById(Integer id) {
        Community entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
        return mappers.toResponse(entity);
    }

    public CommunityResponse create(CommunityRequest request) {
        Community entity = Community.builder()
                .nombre(request.getNombre())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public CommunityResponse update(Integer id, CommunityRequest request) {
        Community entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
        entity.setNombre(request.getNombre());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Community entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
        repo.delete(entity);
    }
}
