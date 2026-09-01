package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.InvoicesItemsRequest;
import zentry.back.api.business.dtos.InvoicesItemsResponse;
import zentry.back.api.business.models.InvoicesItems;
import zentry.back.api.business.repositories.InvoicesItemsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class InvoicesItemsService {

    private final InvoicesItemsRepository repo;

    public InvoicesItemsService(InvoicesItemsRepository repo) {
        this.repo = repo;
    }

    public Page<InvoicesItemsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public InvoicesItemsResponse getById(UUID id) {
        InvoicesItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "InvoiceItem not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public InvoicesItemsResponse create(InvoicesItemsRequest request) {
        InvoicesItems entity = InvoicesItems.builder()
                .invoiceId(request.getInvoiceId())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public InvoicesItemsResponse update(UUID id, InvoicesItemsRequest request) {
        InvoicesItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "InvoiceItem not found"));
        entity.setInvoiceId(request.getInvoiceId());
        entity.setDescripcion(request.getDescripcion());
        entity.setPrecio(request.getPrecio());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        InvoicesItems entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "InvoiceItem not found"));
        repo.delete(entity);
    }
}
