package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.WalletTransactionsRequest;
import zentry.back.api.business.dtos.WalletTransactionsResponse;
import zentry.back.api.business.models.WalletTransactions;
import zentry.back.api.business.repositories.WalletTransactionsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class WalletTransactionsService {

    private final WalletTransactionsRepository repo;

    public WalletTransactionsService(WalletTransactionsRepository repo) {
        this.repo = repo;
    }

    public Page<WalletTransactionsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public WalletTransactionsResponse getById(UUID id) {
        WalletTransactions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WalletTransaction not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public WalletTransactionsResponse create(WalletTransactionsRequest request) {
        WalletTransactions entity = WalletTransactions.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public WalletTransactionsResponse update(UUID id, WalletTransactionsRequest request) {
        WalletTransactions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WalletTransaction not found"));
        entity.setUserId(request.getUserId());
        entity.setAmount(request.getAmount());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        WalletTransactions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WalletTransaction not found"));
        repo.delete(entity);
    }
}
