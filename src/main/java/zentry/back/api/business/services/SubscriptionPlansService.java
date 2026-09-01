package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.SubscriptionPlansRequest;
import zentry.back.api.business.dtos.SubscriptionPlansResponse;
import zentry.back.api.business.models.SubscriptionPlans;
import zentry.back.api.business.repositories.SubscriptionPlansRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class SubscriptionPlansService {

    private final SubscriptionPlansRepository repo;

    public SubscriptionPlansService(SubscriptionPlansRepository repo) {
        this.repo = repo;
    }

    public Page<SubscriptionPlansResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SubscriptionPlansResponse getById(UUID id) {
        SubscriptionPlans entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubscriptionPlan not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SubscriptionPlansResponse create(SubscriptionPlansRequest request) {
        if (repo.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A plan with this name already exists");
        }
        SubscriptionPlans entity = SubscriptionPlans.builder()
                .name(request.getName())
                .precio(request.getPrecio())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SubscriptionPlansResponse update(UUID id, SubscriptionPlansRequest request) {
        SubscriptionPlans entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubscriptionPlan not found"));
        entity.setName(request.getName());
        entity.setPrecio(request.getPrecio());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        SubscriptionPlans entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubscriptionPlan not found"));
        repo.delete(entity);
    }
}
