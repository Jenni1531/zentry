package zentry.back.api.analytics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.analytics.dtos.ConversionFunnelRequest;
import zentry.back.api.analytics.dtos.ConversionFunnelResponse;
import zentry.back.api.analytics.models.ConversionFunnel;
import zentry.back.api.analytics.repositories.ConversionFunnelRepository;
<<<<<<< HEAD
import zentry.back.api.analytics.mappers.AnalyticsMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class ConversionFunnelService {

    private final ConversionFunnelRepository repo;

    public ConversionFunnelService(ConversionFunnelRepository repo) {
        this.repo = repo;
    }

    public Page<ConversionFunnelResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(AnalyticsMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ConversionFunnelResponse getById(Integer id) {
        ConversionFunnel entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConversionFunnel not found"));
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ConversionFunnelResponse create(ConversionFunnelRequest request) {
        ConversionFunnel entity = ConversionFunnel.builder()
                .funnelName(request.getFunnelName())
                .steps(request.getSteps())
                .conversions(request.getConversions())
                .build();
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public ConversionFunnelResponse update(Integer id, ConversionFunnelRequest request) {
        ConversionFunnel entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConversionFunnel not found"));
        entity.setFunnelName(request.getFunnelName());
        entity.setSteps(request.getSteps());
        entity.setConversions(request.getConversions());
<<<<<<< HEAD
        return AnalyticsMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer id) {
        ConversionFunnel entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConversionFunnel not found"));
        repo.delete(entity);
    }
}
