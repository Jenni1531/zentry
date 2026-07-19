package zentry.back.api.realtime.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.realtime.dtos.MessageRequest;
import zentry.back.api.realtime.dtos.MessageResponse;
import zentry.back.api.realtime.models.Message;
import zentry.back.api.realtime.repositories.MessageRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class MessageService {

    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public Page<MessageResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public MessageResponse getById(Integer id) {
        Message entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        return mappers.toResponse(entity);
    }

    public MessageResponse create(MessageRequest request) {
        Message entity = Message.builder()
                .conversationId(request.getConversationId())
                .senderId(request.getSenderId())
                .content(request.getContent())
                .type(request.getType())
                .read(request.getRead())
                .build();
        return mappers.toResponse(repo.save(entity));
    }

    public MessageResponse update(Integer id, MessageRequest request) {
        Message entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        entity.setConversationId(request.getConversationId());
        entity.setSenderId(request.getSenderId());
        entity.setContent(request.getContent());
        entity.setType(request.getType());
        entity.setRead(request.getRead());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Message entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        repo.delete(entity);
    }
}
