package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.SearchClickRequest;
import zentry.back.api.analytics.dtos.SearchClickResponse;
import zentry.back.api.analytics.models.SearchClick;
import zentry.back.api.analytics.repositories.SearchClickRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class SearchClickService {

    private final SearchClickRepository repo;

    public SearchClickService(SearchClickRepository repo) {
        this.repo = repo;
    }

    public Page<SearchClickResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SearchClickResponse getById(Integer id) {
        SearchClick entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SearchClick not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SearchClickResponse create(SearchClickRequest request) {
        SearchClick entity = SearchClick.builder()
                .searchLogId(request.getSearchLogId())
                .resultClicked(request.getResultClicked())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public SearchClickResponse update(Integer id, SearchClickRequest request) {
        SearchClick entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SearchClick not found"));
        entity.setSearchLogId(request.getSearchLogId());
        entity.setResultClicked(request.getResultClicked());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        SearchClick entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SearchClick not found"));
        repo.delete(entity);
    }
}
