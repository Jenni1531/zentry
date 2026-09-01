package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.PaymentMethodsRequest;
import zentry.back.api.business.dtos.PaymentMethodsResponse;
import zentry.back.api.business.models.PaymentMethods;
import zentry.back.api.business.repositories.PaymentMethodsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class PaymentMethodsService {

    private final PaymentMethodsRepository repo;

    public PaymentMethodsService(PaymentMethodsRepository repo) {
        this.repo = repo;
    }

    public Page<PaymentMethodsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PaymentMethodsResponse getById(UUID id) {
        PaymentMethods entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentMethod not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PaymentMethodsResponse create(PaymentMethodsRequest request) {
        if (repo.existsByUserIdAndMetodo(request.getUserId(), request.getMetodo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment method already registered for this user");
        }
        PaymentMethods entity = PaymentMethods.builder()
                .userId(request.getUserId())
                .metodo(request.getMetodo())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PaymentMethodsResponse update(UUID id, PaymentMethodsRequest request) {
        PaymentMethods entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentMethod not found"));
        entity.setUserId(request.getUserId());
        entity.setMetodo(request.getMetodo());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        PaymentMethods entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentMethod not found"));
        repo.delete(entity);
    }
}
