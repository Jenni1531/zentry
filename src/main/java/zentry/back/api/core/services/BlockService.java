package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.BlockRequest;
import zentry.back.api.core.dtos.BlockResponse;
import zentry.back.api.core.models.Block;
import zentry.back.api.core.models.Block.BlockId;
import zentry.back.api.core.repositories.BlockRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class BlockService {

    private final BlockRepository repo;

    public BlockService(BlockRepository repo) {
        this.repo = repo;
    }

    public Page<BlockResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public BlockResponse getById(Integer userId, Integer blockedId) {
        BlockId id = new BlockId(userId, blockedId);
        Block entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Block not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public BlockResponse create(BlockRequest request) {
        BlockId id = new BlockId(request.getUserId(), request.getBlockedId());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Block already exists");
        }
        Block entity = Block.builder()
                .userId(request.getUserId())
                .blockedId(request.getBlockedId())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer userId, Integer blockedId) {
        BlockId id = new BlockId(userId, blockedId);
        Block entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Block not found"));
        repo.delete(entity);
    }
}
