package zentry.back.api.realtime.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.realtime.dtos.ConversationRequest;
import zentry.back.api.realtime.dtos.ConversationResponse;
import zentry.back.api.realtime.models.Conversation;
import zentry.back.api.realtime.repositories.ConversationRepository;
import zentry.back.api.global.mappers;

@Service
public class ConversationService {

    private final ConversationRepository repo;

    public ConversationService(ConversationRepository repo) {
        this.repo = repo;
    }

    public Page<ConversationResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public ConversationResponse getById(Integer id) {
        Conversation entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        return mappers.toResponse(entity);
    }

    public ConversationResponse create(ConversationRequest request) {
        Conversation entity = Conversation.builder()
                .isGroup(request.getIsGroup())
                .name(request.getName())
                .createdBy(request.getCreatedBy())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public ConversationResponse update(Integer id, ConversationRequest request) {
        Conversation entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        entity.setIsGroup(request.getIsGroup());
        entity.setName(request.getName());
        entity.setCreatedBy(request.getCreatedBy());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Conversation entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
        repo.delete(entity);
    }
}
