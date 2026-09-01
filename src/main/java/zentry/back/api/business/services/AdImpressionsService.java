package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.AdImpressionsRequest;
import zentry.back.api.business.dtos.AdImpressionsResponse;
import zentry.back.api.business.models.AdImpressions;
import zentry.back.api.business.repositories.AdImpressionsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class AdImpressionsService {

    private final AdImpressionsRepository repo;

    public AdImpressionsService(AdImpressionsRepository repo) {
        this.repo = repo;
    }

    public Page<AdImpressionsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AdImpressionsResponse getById(UUID id) {
        AdImpressions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdImpression not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AdImpressionsResponse create(AdImpressionsRequest request) {
        AdImpressions entity = AdImpressions.builder()
                .campaignId(request.getCampaignId())
                .vistas(request.getVistas())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AdImpressionsResponse update(UUID id, AdImpressionsRequest request) {
        AdImpressions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdImpression not found"));
        entity.setCampaignId(request.getCampaignId());
        entity.setVistas(request.getVistas());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        AdImpressions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AdImpression not found"));
        repo.delete(entity);
    }
}
