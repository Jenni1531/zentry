package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.AiTrainingLogRequest;
import zentry.back.api.analytics.dtos.AiTrainingLogResponse;
import zentry.back.api.analytics.models.AiTrainingLog;
import zentry.back.api.analytics.repositories.AiTrainingLogRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class AiTrainingLogService {

    private final AiTrainingLogRepository repo;

    public AiTrainingLogService(AiTrainingLogRepository repo) {
        this.repo = repo;
    }

    public Page<AiTrainingLogResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AiTrainingLogResponse getById(Integer id) {
        AiTrainingLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AiTrainingLog not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AiTrainingLogResponse create(AiTrainingLogRequest request) {
        AiTrainingLog entity = AiTrainingLog.builder()
                .model(request.getModel())
                .inputData(request.getInputData())
                .outputData(request.getOutputData())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public AiTrainingLogResponse update(Integer id, AiTrainingLogRequest request) {
        AiTrainingLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AiTrainingLog not found"));
        entity.setModel(request.getModel());
        entity.setInputData(request.getInputData());
        entity.setOutputData(request.getOutputData());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        AiTrainingLog entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AiTrainingLog not found"));
        repo.delete(entity);
    }
}
