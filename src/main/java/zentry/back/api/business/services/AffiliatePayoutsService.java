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
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class AffiliatePayoutsService {

    private final AffiliatePayoutsRepository repo;

    public AffiliatePayoutsService(AffiliatePayoutsRepository repo) {
        this.repo = repo;
    }

    public Page<AffiliatePayoutsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AffiliatePayoutsResponse getById(UUID id) {
        AffiliatePayouts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AffiliatePayout not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AffiliatePayoutsResponse create(AffiliatePayoutsRequest request) {
        AffiliatePayouts entity = AffiliatePayouts.builder()
                .affiliateId(request.getAffiliateId())
                .amount(request.getAmount())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AffiliatePayoutsResponse update(UUID id, AffiliatePayoutsRequest request) {
        AffiliatePayouts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AffiliatePayout not found"));
        entity.setAffiliateId(request.getAffiliateId());
        entity.setAmount(request.getAmount());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        AffiliatePayouts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AffiliatePayout not found"));
        repo.delete(entity);
    }
}
