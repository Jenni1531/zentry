package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.MarketplaceProductsRequest;
import zentry.back.api.business.dtos.MarketplaceProductsResponse;
import zentry.back.api.business.models.MarketplaceProducts;
import zentry.back.api.business.repositories.MarketplaceProductsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class MarketplaceProductsService {

    private final MarketplaceProductsRepository repo;

    public MarketplaceProductsService(MarketplaceProductsRepository repo) {
        this.repo = repo;
    }

    public Page<MarketplaceProductsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public MarketplaceProductsResponse getById(UUID id) {
        MarketplaceProducts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MarketplaceProduct not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public MarketplaceProductsResponse create(MarketplaceProductsRequest request) {
        if (repo.existsBySellerIdAndNombre(request.getSellerId(), request.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller already has a product with this name");
        }
        MarketplaceProducts entity = MarketplaceProducts.builder()
                .sellerId(request.getSellerId())
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public MarketplaceProductsResponse update(UUID id, MarketplaceProductsRequest request) {
        MarketplaceProducts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MarketplaceProduct not found"));
        entity.setSellerId(request.getSellerId());
        entity.setNombre(request.getNombre());
        entity.setPrecio(request.getPrecio());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        MarketplaceProducts entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MarketplaceProduct not found"));
        repo.delete(entity);
    }
}
