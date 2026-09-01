package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.OrderItemsRequest;
import zentry.back.api.business.dtos.OrderItemsResponse;
import zentry.back.api.business.models.OrderItems;
import zentry.back.api.business.models.OrderItems.OrderItemsId;
import zentry.back.api.business.repositories.OrderItemsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class OrderItemsService {

    private final OrderItemsRepository repo;

    public OrderItemsService(OrderItemsRepository repo) {
        this.repo = repo;
    }

    public Page<OrderItemsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public OrderItemsResponse getById(UUID orderId, UUID productId) {
        OrderItemsId id = new OrderItemsId(orderId, productId);
        OrderItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderItem not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public OrderItemsResponse create(OrderItemsRequest request) {
        if (repo.existsByOrderIdAndProductId(request.getOrderId(), request.getProductId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item already exists in this order");
        }
        OrderItems entity = OrderItems.builder()
                .orderId(request.getOrderId())
                .productId(request.getProductId())
                .cantidad(request.getCantidad())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public OrderItemsResponse update(UUID orderId, UUID productId, OrderItemsRequest request) {
        OrderItemsId id = new OrderItemsId(orderId, productId);
        OrderItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderItem not found"));
        entity.setCantidad(request.getCantidad());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID orderId, UUID productId) {
        OrderItemsId id = new OrderItemsId(orderId, productId);
        OrderItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderItem not found"));
        repo.delete(entity);
    }
}
