package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.SeriesChapterRequest;
import zentry.back.api.core.dtos.SeriesChapterResponse;
import zentry.back.api.core.models.SeriesChapter;
import zentry.back.api.core.repositories.SeriesChapterRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class SeriesChapterService {

    private final SeriesChapterRepository repo;

    public SeriesChapterService(SeriesChapterRepository repo) {
        this.repo = repo;
    }

    public Page<SeriesChapterResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SeriesChapterResponse getById(Integer id) {
        SeriesChapter entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SeriesChapter not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SeriesChapterResponse create(SeriesChapterRequest request) {
        SeriesChapter entity = SeriesChapter.builder()
                .seriesId(request.getSeriesId())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SeriesChapterResponse update(Integer id, SeriesChapterRequest request) {
        SeriesChapter entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SeriesChapter not found"));
        entity.setSeriesId(request.getSeriesId());
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        SeriesChapter entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SeriesChapter not found"));
        repo.delete(entity);
    }
}
