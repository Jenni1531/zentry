package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import zentry.back.api.core.dtos.CommunityMemberRequest;
import zentry.back.api.core.dtos.CommunityMemberResponse;
import zentry.back.api.core.models.CommunityMember;
import zentry.back.api.core.models.CommunityMember.CommunityMemberId;
import zentry.back.api.core.repositories.CommunityMemberRepository;
<<<<<<< HEAD
import zentry.back.api.core.mappers.CoreMappers;
=======
import zentry.back.api.global.mappers;
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f

@Service
@SuppressWarnings("null")
public class CommunityMemberService {

    private final CommunityMemberRepository repo;

    public CommunityMemberService(CommunityMemberRepository repo) {
        this.repo = repo;
    }

    public Page<CommunityMemberResponse> list(Pageable pageable) {
<<<<<<< HEAD
        return repo.findAll(pageable).map(CoreMappers::toResponse);
=======
        return repo.findAll(pageable).map(mappers::toResponse);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommunityMemberResponse getById(Integer communityId, Integer userId) {
        CommunityMemberId id = new CommunityMemberId(communityId, userId);
        CommunityMember entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommunityMember not found"));
<<<<<<< HEAD
        return CoreMappers.toResponse(entity);
=======
        return mappers.toResponse(entity);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public CommunityMemberResponse create(CommunityMemberRequest request) {
        CommunityMemberId id = new CommunityMemberId(request.getCommunityId(), request.getUserId());
        if (repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CommunityMember already exists");
        }
        CommunityMember entity = CommunityMember.builder()
                .communityId(request.getCommunityId())
                .userId(request.getUserId())
                .build();
<<<<<<< HEAD
        return CoreMappers.toResponse(repo.save(entity));
=======
        return mappers.toResponse(repo.save(entity));
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    }

    public void delete(Integer communityId, Integer userId) {
        CommunityMemberId id = new CommunityMemberId(communityId, userId);
        CommunityMember entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CommunityMember not found"));
        repo.delete(entity);
    }
}
