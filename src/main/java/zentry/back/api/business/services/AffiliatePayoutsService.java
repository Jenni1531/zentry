package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.AffiliatePayoutsRequest;
import zentry.back.api.business.dtos.AffiliatePayoutsResponse;
import zentry.back.api.business.models.AffiliatePayouts;
import zentry.back.api.business.repositories.AffiliatePayoutsRepository;
import zentry.back.api.global.mappers;

import java.util.UUID;

@Service
public class AffiliatePayoutsService {

    private final AffiliatePayoutsRepository repo;

    public AffiliatePayoutsService(AffiliatePayoutsRepository repo) {
        this.repo = repo;
    }

    public Page<AffiliatePayoutsResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public AffiliatePayoutsResponse getById(UUID id) {
        AffiliatePayouts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AffiliatePayout not found"));
        return mappers.toResponse(entity);
    }

    public AffiliatePayoutsResponse create(AffiliatePayoutsRequest request) {
        AffiliatePayouts entity = AffiliatePayouts.builder()
                .affiliateId(request.getAffiliateId())
                .amount(request.getAmount())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public AffiliatePayoutsResponse update(UUID id, AffiliatePayoutsRequest request) {
        AffiliatePayouts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AffiliatePayout not found"));
        entity.setAffiliateId(request.getAffiliateId());
        entity.setAmount(request.getAmount());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(UUID id) {
        AffiliatePayouts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AffiliatePayout not found"));
        repo.delete(entity);
    }
}
