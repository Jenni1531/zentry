package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import zentry.back.api.core.models.User;
import zentry.back.api.core.dtos.CommunityRequest;
import zentry.back.api.core.dtos.CommunityResponse;
import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.models.Community;
import zentry.back.api.core.models.CommunityMember;
import zentry.back.api.core.repositories.CommunityMemberRepository;
import zentry.back.api.core.repositories.CommunityRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.core.mappers.CoreMappers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
@SuppressWarnings("null")
public class CommunityService {

    private final CommunityRepository repo;
    private final CommunityMemberRepository memberRepo;
    private final UserRepository userRepo;
    private final PostService postService;
    private final GamificationEventService gamificationEventService;
    private final NotificationService notificationService;
    private final zentry.back.api.core.repositories.ProfileRepository profileRepo;
    private final CosmeticsService cosmeticsService;

    public CommunityService(CommunityRepository repo, CommunityMemberRepository memberRepo, UserRepository userRepo,
                             PostService postService, GamificationEventService gamificationEventService,
                             NotificationService notificationService, zentry.back.api.core.repositories.ProfileRepository profileRepo,
                             CosmeticsService cosmeticsService) {
        this.notificationService = notificationService;
        this.profileRepo = profileRepo;
        this.cosmeticsService = cosmeticsService;
        this.repo = repo;
        this.memberRepo = memberRepo;
        this.userRepo = userRepo;
        this.postService = postService;
        this.gamificationEventService = gamificationEventService;
    }

    public Page<CommunityResponse> list(String search, Pageable pageable, String currentUserEmail) {
        Page<Community> page;
        if (search != null && !search.isBlank()) {
            page = repo.findSearch(search.trim(), pageable);
        } else {
            page = repo.findAll(pageable);
        }

        User currentUser = currentUserEmail != null ? userRepo.findByEmail(currentUserEmail).orElse(null) : null;
        return page.map(c -> enrichResponse(c, currentUser));
    }

    public CommunityResponse getById(Integer id) {
        Community entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidad no encontrada"));
        return enrichResponse(entity, null);
    }

    public CommunityResponse getByIdentifier(String identifier, String currentUserEmail) {
        if (identifier == null || identifier.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identificador de comunidad vacío");
        }
        Community entity = findEntityByIdentifier(identifier);
        User currentUser = currentUserEmail != null ? userRepo.findByEmail(currentUserEmail).orElse(null) : null;
        return enrichResponse(entity, currentUser);
    }

    public CommunityResponse create(String userEmail, CommunityRequest request) {
        User creator = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = generateSlug(request.getNombre());
        }

        Community entity = Community.builder()
                .nombre(request.getNombre())
                .slug(slug)
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .avatarUrl(request.getAvatarUrl())
                .bannerUrl(request.getBannerUrl())
                .imageUrl(request.getAvatarUrl() != null ? request.getAvatarUrl() : request.getBannerUrl())
                .rules(request.getRules() != null ? request.getRules() : new ArrayList<>())
                .creatorId(creator.getId())
                .ownerUsername(creator.getHandle())
                .privacy("private".equalsIgnoreCase(request.getPrivacy()) ? "private" : "public")
                .build();
        
        Community savedCommunity = repo.save(entity);

        CommunityMember founder = CommunityMember.builder()
                .communityId(savedCommunity.getId())
                .userId(creator.getId())
                .role("ADMIN")
                .joinedAt(LocalDateTime.now())
                .build();
        
        memberRepo.save(founder);
        gamificationEventService.recordAchievementProgress(creator.getId(), "create_community", 1);
        return enrichResponse(savedCommunity, creator);
    }

    public CommunityResponse updateConfig(String identifier, String userEmail, CommunityRequest request, MultipartFile avatar, MultipartFile banner) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Community entity = findEntityByIdentifier(identifier);

        // Portada, ícono, nombre, reglas y privacidad: solo administradores de la comunidad
        requireAdmin(entity, user);

        if (request != null) {
            if (request.getNombre() != null && !request.getNombre().isBlank()) {
                entity.setNombre(request.getNombre());
            }
            if (request.getDescripcion() != null) {
                entity.setDescripcion(request.getDescripcion());
            }
            if (request.getRules() != null) {
                entity.setRules(request.getRules());
            }
            if (request.getAvatarUrl() != null) {
                entity.setAvatarUrl(request.getAvatarUrl());
            }
            if (request.getBannerUrl() != null) {
                entity.setBannerUrl(request.getBannerUrl());
            }
            if (request.getCategoria() != null) {
                entity.setCategoria(request.getCategoria());
            }
            if (request.getPrivacy() != null) {
                entity.setPrivacy("private".equalsIgnoreCase(request.getPrivacy()) ? "private" : "public");
            }
        }

        if (avatar != null && !avatar.isEmpty()) {
            String avatarFilename = saveImage(avatar, entity.getSlug(), "avatar");
            entity.setAvatarUrl("/uploads/communities/" + avatarFilename);
            entity.setImageUrl("/uploads/communities/" + avatarFilename);
        }

        if (banner != null && !banner.isEmpty()) {
            String bannerFilename = saveImage(banner, entity.getSlug(), "banner");
            entity.setBannerUrl("/uploads/communities/" + bannerFilename);
        }

        Community saved = repo.save(entity);
        return enrichResponse(saved, user);
    }

    public CommunityResponse joinCommunity(String identifier, String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Community community = findEntityByIdentifier(identifier);

        boolean alreadyMember = memberRepo.existsByCommunityIdAndUserId(community.getId(), user.getId());
        if (!alreadyMember) {
            boolean isPrivate = "private".equals(community.getPrivacy());
            memberRepo.save(CommunityMember.builder()
                    .communityId(community.getId())
                    .userId(user.getId())
                    .role(isPrivate ? "PENDING" : "MEMBER")
                    .joinedAt(LocalDateTime.now())
                    .build());

            if (isPrivate) {
                // Grupo privado: los administradores deben aprobar la solicitud
                for (CommunityMember admin : memberRepo.findByCommunityIdAndRole(community.getId(), "ADMIN")) {
                    notificationService.notify(admin.getUserId(), "community",
                            "@" + user.getHandle() + " quiere unirse a " + community.getNombre(),
                            user.getHandle(), null, community.getId());
                }
            } else {
                gamificationEventService.recordMissionProgress(user.getId(), "visit_community", 1);
                gamificationEventService.recordAchievementProgress(user.getId(), "join_communities", 1);
            }
        }

        return enrichResponse(community, user);
    }

    public CommunityResponse leaveCommunity(String identifier, String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Community community = findEntityByIdentifier(identifier);

        Optional<CommunityMember> memberOpt = memberRepo.findByCommunityIdAndUserId(community.getId(), user.getId());
        if (memberOpt.isPresent() && "ADMIN".equals(memberOpt.get().getRole())
                && memberRepo.countByCommunityIdAndRole(community.getId(), "ADMIN") <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Eres el único administrador: nombra a otro antes de salir");
        }
        memberOpt.ifPresent(memberRepo::delete);

        return enrichResponse(community, user);
    }

    public PostResponse createPostInCommunity(String identifier, String userEmail, PostRequest request) {
        Community community = findEntityByIdentifier(identifier);
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión"));
        if (!isActiveMember(community, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Únete a la comunidad para publicar");
        }
        return postService.create(userEmail, request, community.getId());
    }

    public org.springframework.data.domain.Page<PostResponse> getCommunityPosts(String identifier, org.springframework.data.domain.Pageable pageable, String viewerEmail) {
        Community community = findEntityByIdentifier(identifier);
        if ("private".equals(community.getPrivacy())) {
            User viewer = viewerEmail != null ? userRepo.findByEmail(viewerEmail).orElse(null) : null;
            if (!isActiveMember(community, viewer)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este grupo es privado: solo sus miembros ven las publicaciones");
            }
        }
        return postService.getPostsByCommunity(community.getId(), pageable, viewerEmail);
    }

    // ---------------- Miembros (estilo grupo de Facebook) ----------------

    public java.util.List<Map<String, Object>> listMembers(String identifier, String viewerEmail) {
        Community community = findEntityByIdentifier(identifier);
        User viewer = viewerEmail != null ? userRepo.findByEmail(viewerEmail).orElse(null) : null;
        boolean viewerIsAdmin = isAdmin(community, viewer);
        if ("private".equals(community.getPrivacy()) && !isActiveMember(community, viewer)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este grupo es privado");
        }
        java.util.List<CommunityMember> members = memberRepo.findByCommunityIdOrderByJoinedAtAsc(community.getId());
        java.util.Set<Integer> ids = members.stream().map(CommunityMember::getUserId).collect(java.util.stream.Collectors.toSet());
        Map<Integer, User> users = userRepo.findAllById(ids).stream().collect(java.util.stream.Collectors.toMap(User::getId, u -> u));
        Map<Integer, zentry.back.api.core.models.Profile> profiles = profileRepo.findByUserIdIn(ids).stream()
                .collect(java.util.stream.Collectors.toMap(zentry.back.api.core.models.Profile::getUserId, pr -> pr, (a, b) -> a));
        Map<Integer, zentry.back.api.core.dtos.CosmeticsResponse> cosmetics = cosmeticsService.forUsers(ids);

        java.util.List<Map<String, Object>> result = new ArrayList<>();
        for (CommunityMember m : members) {
            // Las solicitudes pendientes solo las ven los administradores
            if ("PENDING".equals(m.getRole()) && !viewerIsAdmin) continue;
            User u = users.get(m.getUserId());
            if (u == null) continue;
            var profile = profiles.get(u.getId());
            Map<String, Object> row = new HashMap<>();
            row.put("userId", u.getId());
            row.put("username", u.getHandle());
            row.put("name", profile != null && profile.getName() != null ? profile.getName() : u.getHandle());
            row.put("avatarUrl", profile != null ? profile.getAvatarUrl() : null);
            row.put("role", m.getRole());
            row.put("isOwner", u.getId().equals(community.getCreatorId()));
            row.put("joinedAt", m.getJoinedAt());
            row.put("cosmetics", cosmetics.get(u.getId()));
            result.add(row);
        }
        return result;
    }

    /** action: approve | reject | promote | demote | remove */
    public CommunityResponse manageMember(String identifier, String adminEmail, Integer targetUserId, String action) {
        Community community = findEntityByIdentifier(identifier);
        User admin = userRepo.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión"));
        requireAdmin(community, admin);
        CommunityMember member = memberRepo.findByCommunityIdAndUserId(community.getId(), targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Miembro no encontrado"));
        if (targetUserId.equals(community.getCreatorId()) && !"approve".equals(action)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede modificar al creador del grupo");
        }

        switch (action == null ? "" : action) {
            case "approve" -> {
                member.setRole("MEMBER");
                memberRepo.save(member);
                notificationService.notify(targetUserId, "community",
                        "✅ Te aceptaron en el grupo " + community.getNombre(), admin.getHandle(), null, community.getId());
                gamificationEventService.recordMissionProgress(targetUserId, "visit_community", 1);
                gamificationEventService.recordAchievementProgress(targetUserId, "join_communities", 1);
            }
            case "reject", "remove" -> memberRepo.delete(member);
            case "promote" -> { member.setRole("ADMIN"); memberRepo.save(member); }
            case "demote" -> { member.setRole("MEMBER"); memberRepo.save(member); }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Acción no válida");
        }
        return enrichResponse(community, admin);
    }

    private boolean isAdmin(Community community, User user) {
        if (user == null) return false;
        if (user.getId().equals(community.getCreatorId())) return true;
        return memberRepo.findByCommunityIdAndUserId(community.getId(), user.getId())
                .map(m -> "ADMIN".equals(m.getRole())).orElse(false);
    }

    private boolean isActiveMember(Community community, User user) {
        if (user == null) return false;
        return memberRepo.findByCommunityIdAndUserId(community.getId(), user.getId())
                .map(m -> !"PENDING".equals(m.getRole())).orElse(false);
    }

    private void requireAdmin(Community community, User user) {
        if (!isAdmin(community, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los administradores de la comunidad pueden hacer esto");
        }
    }

    public Map<String, Object> toggleNotifications(String identifier, String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        Community community = findEntityByIdentifier(identifier);

        CommunityMember member = memberRepo.findByCommunityIdAndUserId(community.getId(), user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes ser miembro de la comunidad para gestionar sus notificaciones"));

        boolean nextValue = !Boolean.TRUE.equals(member.getNotificationsEnabled());
        member.setNotificationsEnabled(nextValue);
        memberRepo.save(member);

        Map<String, Object> response = new HashMap<>();
        response.put("communityId", community.getId());
        response.put("notificationsEnabled", nextValue);
        response.put("message", nextValue ? "Notificaciones activadas" : "Notificaciones desactivadas");
        return response;
    }

    /** Antes cualquiera podía borrar cualquier comunidad: ahora solo su creador */
    @org.springframework.transaction.annotation.Transactional
    public void delete(Integer id, String userEmail) {
        Community entity = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidad no encontrada"));
        User user = userEmail != null ? userRepo.findByEmail(userEmail).orElse(null) : null;
        if (user == null || !user.getId().equals(entity.getCreatorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el creador puede eliminar la comunidad");
        }
        memberRepo.deleteByCommunityId(id);
        repo.delete(entity);
    }

    private CommunityResponse enrichResponse(Community community, User currentUser) {
        CommunityResponse response = CoreMappers.toResponse(community);

        Integer membersCount = memberRepo.countByCommunityIdAndRoleNot(community.getId(), "PENDING");
        response.setMembersCount(membersCount != null ? membersCount : 0);
        response.setPrivacy(community.getPrivacy() != null ? community.getPrivacy() : "public");

        String role = currentUser == null ? null
                : memberRepo.findByCommunityIdAndUserId(community.getId(), currentUser.getId()).map(CommunityMember::getRole).orElse(null);
        boolean admin = isAdmin(community, currentUser);
        response.setMyRole(role);
        response.setIsJoined(role != null && !"PENDING".equals(role));
        response.setHasPendingRequest("PENDING".equals(role));
        response.setIsAdmin(admin);
        response.setIsOwner(currentUser != null && currentUser.getId().equals(community.getCreatorId()));
        if (admin) {
            Integer pending = memberRepo.countByCommunityIdAndRole(community.getId(), "PENDING");
            response.setPendingCount(pending != null ? pending : 0);
        }

        return response;
    }

    private Community findEntityByIdentifier(String identifier) {
        try {
            Integer id = Integer.parseInt(identifier);
            Optional<Community> opt = repo.findById(id);
            if (opt.isPresent()) return opt.get();
        } catch (NumberFormatException ignored) {}

        String normalized = identifier.replace("-", " ");
        List<Community> matches = repo.findAllBySlugOrNombreMatch(identifier);
        if (!matches.isEmpty()) return matches.get(0);
        return repo.findByNombreIgnoreCase(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidad no encontrada: " + identifier));
    }

    private String generateSlug(String text) {
        if (text == null || text.isBlank()) return UUID.randomUUID().toString().substring(0, 8);
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
        return slug.isEmpty() ? UUID.randomUUID().toString().substring(0, 8) : slug;
    }

    private String saveImage(MultipartFile file, String identifier, String type) {
        try {
            Path uploadPath = Paths.get("uploads/communities");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String newFilename = identifier + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 5) + extension;

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar imagen de comunidad", e);
        }
    }
}
