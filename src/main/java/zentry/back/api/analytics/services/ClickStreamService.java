package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.ClickStreamRequest;
import zentry.back.api.analytics.dtos.ClickStreamResponse;
import zentry.back.api.analytics.models.ClickStream;
import zentry.back.api.analytics.repositories.ClickStreamRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class ClickStreamService {

    private final ClickStreamRepository repo;

    public ClickStreamService(ClickStreamRepository repo) {
        this.repo = repo;
    }

    public Page<ClickStreamResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ClickStreamResponse getById(Integer id) {
        ClickStream entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ClickStream not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ClickStreamResponse create(ClickStreamRequest request) {
        ClickStream entity = ClickStream.builder()
                .userId(request.getUserId())
                .element(request.getElement())
                .page(request.getPage())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ClickStreamResponse update(Integer id, ClickStreamRequest request) {
        ClickStream entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ClickStream not found"));
        entity.setUserId(request.getUserId());
        entity.setElement(request.getElement());
        entity.setPage(request.getPage());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        ClickStream entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ClickStream not found"));
        repo.delete(entity);
    }
}
