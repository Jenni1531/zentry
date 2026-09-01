package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.AdsCampaignsRequest;
import zentry.back.api.business.dtos.AdsCampaignsResponse;
import zentry.back.api.business.models.AdsCampaigns;
import zentry.back.api.business.repositories.AdsCampaignsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class AdsCampaignsService {

    private final AdsCampaignsRepository repo;

    public AdsCampaignsService(AdsCampaignsRepository repo) {
        this.repo = repo;
    }

    public Page<AdsCampaignsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AdsCampaignsResponse getById(UUID id) {
        AdsCampaigns entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdsCampaign not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AdsCampaignsResponse create(AdsCampaignsRequest request) {
        if (repo.existsByUserIdAndNombre(request.getUserId(), request.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a campaign with this name");
        }
        AdsCampaigns entity = AdsCampaigns.builder()
                .userId(request.getUserId())
                .nombre(request.getNombre())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AdsCampaignsResponse update(UUID id, AdsCampaignsRequest request) {
        AdsCampaigns entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdsCampaign not found"));
        entity.setUserId(request.getUserId());
        entity.setNombre(request.getNombre());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        AdsCampaigns entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdsCampaign not found"));
        repo.delete(entity);
    }
}
