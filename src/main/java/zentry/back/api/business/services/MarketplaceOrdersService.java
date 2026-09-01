package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.MarketplaceOrdersRequest;
import zentry.back.api.business.dtos.MarketplaceOrdersResponse;
import zentry.back.api.business.models.MarketplaceOrders;
import zentry.back.api.business.repositories.MarketplaceOrdersRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class MarketplaceOrdersService {

    private final MarketplaceOrdersRepository repo;

    public MarketplaceOrdersService(MarketplaceOrdersRepository repo) {
        this.repo = repo;
    }

    public Page<MarketplaceOrdersResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public MarketplaceOrdersResponse getById(UUID id) {
        MarketplaceOrders entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MarketplaceOrder not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public MarketplaceOrdersResponse create(MarketplaceOrdersRequest request) {
        MarketplaceOrders entity = MarketplaceOrders.builder()
                .buyerId(request.getBuyerId())
                .total(request.getTotal())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public MarketplaceOrdersResponse update(UUID id, MarketplaceOrdersRequest request) {
        MarketplaceOrders entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MarketplaceOrder not found"));
        entity.setBuyerId(request.getBuyerId());
        entity.setTotal(request.getTotal());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        MarketplaceOrders entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MarketplaceOrder not found"));
        repo.delete(entity);
    }
}
