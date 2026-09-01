package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.PredictionHistoryRequest;
import zentry.back.api.ai.dtos.PredictionHistoryResponse;
import zentry.back.api.ai.models.PredictionHistory;
import zentry.back.api.ai.repositories.PredictionHistoryRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class PredictionHistoryService {

    private final PredictionHistoryRepository repo;

    public PredictionHistoryService(PredictionHistoryRepository repo) {
        this.repo = repo;
    }

    public Page<PredictionHistoryResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PredictionHistoryResponse getById(UUID id) {
        PredictionHistory entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PredictionHistory not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PredictionHistoryResponse create(PredictionHistoryRequest request) {
        PredictionHistory entity = PredictionHistory.builder()
                .predictionId(request.getPredictionId())
                .fecha(request.getFecha())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public PredictionHistoryResponse update(UUID id, PredictionHistoryRequest request) {
        PredictionHistory entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PredictionHistory not found"));
        entity.setPredictionId(request.getPredictionId());
        entity.setFecha(request.getFecha());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        PredictionHistory entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PredictionHistory not found"));
        repo.delete(entity);
    }
}
