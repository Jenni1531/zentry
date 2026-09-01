package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.PayoutRequestsRequest;
import zentry.back.api.business.dtos.PayoutRequestsResponse;
import zentry.back.api.business.models.PayoutRequests;
import zentry.back.api.business.repositories.PayoutRequestsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class PayoutRequestsService {

    private final PayoutRequestsRepository repo;

    public PayoutRequestsService(PayoutRequestsRepository repo) {
        this.repo = repo;
    }

    public Page<PayoutRequestsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PayoutRequestsResponse getById(UUID id) {
        PayoutRequests entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PayoutRequest not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PayoutRequestsResponse create(PayoutRequestsRequest request) {
        PayoutRequests entity = PayoutRequests.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PayoutRequestsResponse update(UUID id, PayoutRequestsRequest request) {
        PayoutRequests entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PayoutRequest not found"));
        entity.setUserId(request.getUserId());
        entity.setAmount(request.getAmount());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        PayoutRequests entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PayoutRequest not found"));
        repo.delete(entity);
    }
}
