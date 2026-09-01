package zentry.back.api.business.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.business.dtos.CommissionsRequest;
import zentry.back.api.business.dtos.CommissionsResponse;
import zentry.back.api.business.models.Commissions;
import zentry.back.api.business.repositories.CommissionsRepository;
<<<<<<< HEAD
import zentry.back.api.business.mappers.BusinessMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class CommissionsService {

    private final CommissionsRepository repo;

    public CommissionsService(CommissionsRepository repo) {
        this.repo = repo;
    }

    public Page<CommissionsResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(BusinessMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommissionsResponse getById(UUID id) {
        Commissions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commission not found"));
<<<<<<< HEAD
        return BusinessMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommissionsResponse create(CommissionsRequest request) {
        Commissions entity = Commissions.builder()
                .porcentaje(request.getPorcentaje())
                .build();
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommissionsResponse update(UUID id, CommissionsRequest request) {
        Commissions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commission not found"));
        entity.setPorcentaje(request.getPorcentaje());
<<<<<<< HEAD
        return BusinessMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        Commissions entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commission not found"));
        repo.delete(entity);
    }
}
