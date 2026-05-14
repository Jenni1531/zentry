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
import zentry.back.api.global.mappers;

import java.util.UUID;

@Service
public class SubscriptionPlansService {

    private final SubscriptionPlansRepository repo;

    public SubscriptionPlansService(SubscriptionPlansRepository repo) {
        this.repo = repo;
    }

    public Page<SubscriptionPlansResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public SubscriptionPlansResponse getById(UUID id) {
        SubscriptionPlans entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubscriptionPlan not found"));
        return mappers.toResponse(entity);
    }

    public SubscriptionPlansResponse create(SubscriptionPlansRequest request) {
        if (repo.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A plan with this name already exists");
        }
        SubscriptionPlans entity = SubscriptionPlans.builder()
                .name(request.getName())
                .precio(request.getPrecio())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public SubscriptionPlansResponse update(UUID id, SubscriptionPlansRequest request) {
        SubscriptionPlans entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubscriptionPlan not found"));
        entity.setName(request.getName());
        entity.setPrecio(request.getPrecio());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(UUID id) {
        SubscriptionPlans entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubscriptionPlan not found"));
        repo.delete(entity);
    }
}
