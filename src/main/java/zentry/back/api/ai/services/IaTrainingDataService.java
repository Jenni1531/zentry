package zentry.back.api.ai.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.ai.dtos.IaTrainingDataRequest;
import zentry.back.api.ai.dtos.IaTrainingDataResponse;
import zentry.back.api.ai.models.IaTrainingData;
import zentry.back.api.ai.repositories.IaTrainingDataRepository;
<<<<<<< HEAD
import zentry.back.api.ai.mappers.IaMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

import java.util.UUID;

@Service
@SuppressWarnings("null")
public class IaTrainingDataService {

    private final IaTrainingDataRepository repo;

    public IaTrainingDataService(IaTrainingDataRepository repo) {
        this.repo = repo;
    }

    public Page<IaTrainingDataResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(IaMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaTrainingDataResponse getById(UUID id) {
        IaTrainingData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TrainingData not found"));
<<<<<<< HEAD
        return IaMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaTrainingDataResponse create(IaTrainingDataRequest request) {
        if (repo.existsByDataInput(request.getDataInput())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Training data already exists");
        }
        IaTrainingData entity = IaTrainingData.builder()
                .dataInput(request.getDataInput())
                .dataOutput(request.getDataOutput())
                .build();
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public IaTrainingDataResponse update(UUID id, IaTrainingDataRequest request) {
        IaTrainingData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TrainingData not found"));
        entity.setDataInput(request.getDataInput());
        entity.setDataOutput(request.getDataOutput());
<<<<<<< HEAD
        return IaMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(UUID id) {
        IaTrainingData entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TrainingData not found"));
        repo.delete(entity);
    }
}
