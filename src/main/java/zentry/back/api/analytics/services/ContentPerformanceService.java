package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.ContentPerformanceRequest;
import zentry.back.api.analytics.dtos.ContentPerformanceResponse;
import zentry.back.api.analytics.models.ContentPerformance;
import zentry.back.api.analytics.repositories.ContentPerformanceRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class ContentPerformanceService {

    private final ContentPerformanceRepository repo;

    public ContentPerformanceService(ContentPerformanceRepository repo) {
        this.repo = repo;
    }

    public Page<ContentPerformanceResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ContentPerformanceResponse getById(Integer id) {
        ContentPerformance entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentPerformance not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ContentPerformanceResponse create(ContentPerformanceRequest request) {
        ContentPerformance entity = ContentPerformance.builder()
                .postId(request.getPostId())
                .views(request.getViews())
                .likes(request.getLikes())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ContentPerformanceResponse update(Integer id, ContentPerformanceRequest request) {
        ContentPerformance entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentPerformance not found"));
        entity.setPostId(request.getPostId());
        entity.setViews(request.getViews());
        entity.setLikes(request.getLikes());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        ContentPerformance entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ContentPerformance not found"));
        repo.delete(entity);
    }
}
