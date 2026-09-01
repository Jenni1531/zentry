package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.ForumReplyRequest;
import zentry.back.api.core.dtos.ForumReplyResponse;
import zentry.back.api.core.models.ForumReply;
import zentry.back.api.core.repositories.ForumReplyRepository;
import zentry.back.api.core.mappers.CoreMappers;

@Service
@SuppressWarnings("null")
public class ForumReplyService {

    private final ForumReplyRepository repo;

    public ForumReplyService(ForumReplyRepository repo) {
        this.repo = repo;
    }

    public Page<ForumReplyResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(CoreMappers::toResponse);
    }

    public ForumReplyResponse getById(Integer id) {
        ForumReply entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ForumReply not found"));
        return CoreMappers.toResponse(entity);
    }

    public ForumReplyResponse create(ForumReplyRequest request) {
        ForumReply entity = ForumReply.builder()
                .threadId(request.getThreadId())
                .build();
        return CoreMappers.toResponse(repo.save(entity));
    }

    public ForumReplyResponse update(Integer id, ForumReplyRequest request) {
        ForumReply entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ForumReply not found"));
        entity.setThreadId(request.getThreadId());
        return CoreMappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        ForumReply entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ForumReply not found"));
        repo.delete(entity);
    }
}
