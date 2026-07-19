package zentry.back.api.realtime.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.realtime.dtos.ConversationParticipantRequest;
import zentry.back.api.realtime.dtos.ConversationParticipantResponse;
import zentry.back.api.realtime.models.ConversationParticipant;
import zentry.back.api.realtime.models.ConversationParticipant.ConversationParticipantId;
import zentry.back.api.realtime.repositories.ConversationParticipantRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class ConversationParticipantService {

    private final ConversationParticipantRepository repo;

    public ConversationParticipantService(ConversationParticipantRepository repo) {
        this.repo = repo;
    }

    public Page<ConversationParticipantResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public ConversationParticipantResponse getById(Integer conversationId, Integer userId) {
        ConversationParticipantId id = new ConversationParticipantId(conversationId, userId);
        ConversationParticipant entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConversationParticipant not found"));
        return mappers.toResponse(entity);
    }

    public ConversationParticipantResponse create(ConversationParticipantRequest request) {
        ConversationParticipantId id = new ConversationParticipantId(request.getConversationId(), request.getUserId());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ConversationParticipant already exists");
        }
        ConversationParticipant entity = ConversationParticipant.builder()
                .conversationId(request.getConversationId())
                .userId(request.getUserId())
                .role(request.getRole())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer conversationId, Integer userId) {
        ConversationParticipantId id = new ConversationParticipantId(conversationId, userId);
        ConversationParticipant entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConversationParticipant not found"));
        repo.delete(entity);
    }
}
