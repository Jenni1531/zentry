package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zentry.back.api.core.models.User;
import zentry.back.api.core.dtos.CommunityRequest;
import zentry.back.api.core.dtos.CommunityResponse;
import zentry.back.api.core.models.Community;
import zentry.back.api.core.models.CommunityMember;
import zentry.back.api.core.repositories.CommunityMemberRepository;
import zentry.back.api.core.repositories.CommunityRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.global.mappers;

@Service
@SuppressWarnings("null")
public class CommunityService {

    private final CommunityRepository repo;
    private final CommunityMemberRepository memberRepo;
    private final UserRepository userRepo;

    public CommunityService(CommunityRepository repo, CommunityMemberRepository memberRepo, UserRepository userRepo) {
        this.repo = repo;
        this.memberRepo = memberRepo;
        this.userRepo = userRepo;
    }

    public Page<CommunityResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(mappers::toResponse);
    }

    public CommunityResponse getById(Integer id) {
        Community entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
        return mappers.toResponse(entity);
    }

    public CommunityResponse create(String userEmail, CommunityRequest request) {
        User creator = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Community entity = Community.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .creatorId(creator.getId())
                .build();
        
        Community savedCommunity = repo.save(entity);

        CommunityMember founder = CommunityMember.builder()
                .communityId(savedCommunity.getId())
                .userId(creator.getId())
                .role("ADMIN")
                .build();
        
        memberRepo.save(founder);
        return mappers.toResponse(repo.save(entity));
    }

    public CommunityResponse update(Integer id,String userEmail, CommunityRequest request) {

        Community entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
        entity.setNombre(request.getNombre());
        //entity.setDescripcion(request.getDescripcion());
        //entity.setCategoria(request.getCategoria());
        return mappers.toResponse(repo.save(entity));
    }

    public void delete(Integer id) {
        Community entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
        repo.delete(entity);
    }
}
