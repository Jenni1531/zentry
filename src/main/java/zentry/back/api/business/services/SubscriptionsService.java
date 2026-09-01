package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.SubscriptionsRequest;
import zentry.back.api.business.dtos.SubscriptionsResponse;
import zentry.back.api.business.models.Subscriptions;
import zentry.back.api.business.repositories.SubscriptionsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class SubscriptionsService {

    private final SubscriptionsRepository repo;

    public SubscriptionsService(SubscriptionsRepository repo) {
        this.repo = repo;
    }

    public Page<SubscriptionsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SubscriptionsResponse getById(UUID id) {
        Subscriptions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SubscriptionsResponse create(SubscriptionsRequest request) {
        if (repo.existsByUserIdAndPlanId(request.getUserId(), request.getPlanId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already subscribed to this plan");
        }
        Subscriptions entity = Subscriptions.builder()
                .userId(request.getUserId())
                .planId(request.getPlanId())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SubscriptionsResponse update(UUID id, SubscriptionsRequest request) {
        Subscriptions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        entity.setUserId(request.getUserId());
        entity.setPlanId(request.getPlanId());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        Subscriptions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        repo.delete(entity);
    }
}
