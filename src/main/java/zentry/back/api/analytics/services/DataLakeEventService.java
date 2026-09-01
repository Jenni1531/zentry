package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.DataLakeEventRequest;
import zentry.back.api.analytics.dtos.DataLakeEventResponse;
import zentry.back.api.analytics.models.DataLakeEvent;
import zentry.back.api.analytics.repositories.DataLakeEventRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class DataLakeEventService {

    private final DataLakeEventRepository repo;

    public DataLakeEventService(DataLakeEventRepository repo) {
        this.repo = repo;
    }

    public Page<DataLakeEventResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public DataLakeEventResponse getById(Integer id) {
        DataLakeEvent entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DataLakeEvent not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public DataLakeEventResponse create(DataLakeEventRequest request) {
        DataLakeEvent entity = DataLakeEvent.builder()
                .rawData(request.getRawData())
                .source(request.getSource())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public DataLakeEventResponse update(Integer id, DataLakeEventRequest request) {
        DataLakeEvent entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DataLakeEvent not found"));
        entity.setRawData(request.getRawData());
        entity.setSource(request.getSource());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        DataLakeEvent entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DataLakeEvent not found"));
        repo.delete(entity);
    }
}
