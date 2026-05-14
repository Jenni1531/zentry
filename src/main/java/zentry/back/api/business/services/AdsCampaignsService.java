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
import zentry.back.api.global.mappers;

import java.util.UUID;

@Service
public class AdsCampaignsService {

    private final AdsCampaignsRepository repo;

    public AdsCampaignsService(AdsCampaignsRepository repo) {
        this.repo = repo;
    }

    public Page<AdsCampaignsResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public AdsCampaignsResponse getById(UUID id) {
        AdsCampaigns entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdsCampaign not found"));
        return mappers.toResponse(entity);
    }

    public AdsCampaignsResponse create(AdsCampaignsRequest request) {
        if (repo.existsByUserIdAndNombre(request.getUserId(), request.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a campaign with this name");
        }
        AdsCampaigns entity = AdsCampaigns.builder()
                .userId(request.getUserId())
                .nombre(request.getNombre())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public AdsCampaignsResponse update(UUID id, AdsCampaignsRequest request) {
        AdsCampaigns entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdsCampaign not found"));
        entity.setUserId(request.getUserId());
        entity.setNombre(request.getNombre());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(UUID id) {
        AdsCampaigns entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdsCampaign not found"));
        repo.delete(entity);
    }
}
