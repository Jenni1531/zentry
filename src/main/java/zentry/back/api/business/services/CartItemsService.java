package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.CartItemsRequest;
import zentry.back.api.business.dtos.CartItemsResponse;
import zentry.back.api.business.models.CartItems;
import zentry.back.api.business.models.CartItems.CartItemsId;
import zentry.back.api.business.repositories.CartItemsRepository;
import zentry.back.api.global.mappers;

@Service
public class CartItemsService {

    private final CartItemsRepository repo;

    public CartItemsService(CartItemsRepository repo) {
        this.repo = repo;
    }

    public Page<CartItemsResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public CartItemsResponse getById(Integer cartId, Integer productId) {
        CartItemsId id = new CartItemsId(cartId, productId);
        CartItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CartItem not found"));
        return mappers.toResponse(entity);
    }

    public CartItemsResponse create(CartItemsRequest request) {
        if (repo.existsByCartIdAndProductId(request.getCartId(), request.getProductId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item already exists in this cart");
        }
        CartItems entity = CartItems.builder()
                .cartId(request.getCartId())
                .productId(request.getProductId())
                .cantidad(request.getCantidad())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public CartItemsResponse update(Integer cartId, Integer productId, CartItemsRequest request) {
        CartItemsId id = new CartItemsId(cartId, productId);
        CartItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CartItem not found"));
        entity.setCantidad(request.getCantidad());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer cartId, Integer productId) {
        CartItemsId id = new CartItemsId(cartId, productId);
        CartItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CartItem not found"));
        repo.delete(entity);
    }
}
