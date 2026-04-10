package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.iaModelsRequest;
import zentry.back.api.ai.dtos.iaModelsResponse;
import zentry.back.api.ai.models.IaModels;
import zentry.back.api.ai.repositories.iaModelsRepository;
import zentry.back.api.global.mappers;

import java.util.UUID;

@Service
public class iaModelsService {

    private final iaModelsRepository repo;

    public iaModelsService(iaModelsRepository repo) {
        this.repo = repo;
    }

    public Page<iaModelsResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public iaModelsResponse getById(UUID id) {
        IaModels model = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Model not found"));
        return mappers.toResponse(model);
    }

    public iaModelsResponse create(iaModelsRequest request) {
        if (repo.existsByNombre(request.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model already exists");
        }
        IaModels model = IaModels.builder()
                .nombre(request.getNombre())
                .build();
        return mappers.toResponse(repo.save(model));
    }

    public iaModelsResponse update(UUID id, iaModelsRequest request) {
        IaModels model = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Model not found"));
        model.setNombre(request.getNombre());
        return mappers.toResponse(repo.save(model));
    }

    public void delete(UUID id) {
        IaModels model = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Model not found"));
        repo.delete(model);
    }
}
