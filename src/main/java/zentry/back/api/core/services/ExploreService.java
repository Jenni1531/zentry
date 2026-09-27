package zentry.back.api.core.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import zentry.back.api.core.dtos.*;
import zentry.back.api.core.models.Profile;
import zentry.back.api.core.models.User;
import zentry.back.api.core.repositories.FollowRepository;
import zentry.back.api.core.repositories.PostRepository;
import zentry.back.api.core.repositories.ProfileRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.core.util.ZentryClock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Explorar: tendencias reales (etiquetas más usadas), obras populares, creadores sugeridos
 * y búsqueda unificada (creadores, obras, proyectos públicos, comunidades y tendencias).
 * Antes las tendencias eran datos de demostración fijos y la búsqueda comparaba contra el email.
 */
@Service
public class ExploreService {

    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;
    private final PostRepository postRepo;
    private final FollowRepository followRepo;
    private final PostService postService;
    private final ProjectService projectService;
    private final CommunityService communityService;
    private final CosmeticsService cosmeticsService;

    public ExploreService(UserRepository userRepo, ProfileRepository profileRepo, PostRepository postRepo,
                          FollowRepository followRepo, PostService postService, ProjectService projectService,
                          CommunityService communityService, CosmeticsService cosmeticsService) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
        this.postRepo = postRepo;
        this.followRepo = followRepo;
        this.postService = postService;
        this.projectService = projectService;
        this.communityService = communityService;
        this.cosmeticsService = cosmeticsService;
    }

    // ------------------------------------------------------------------ Tendencias

    /** Etiquetas más usadas en los últimos 30 días; "en llamas" si tuvieron actividad en las últimas 48 h */
    public List<TrendingTopicResponse> getTrendingTopics() {
        LocalDateTime now = LocalDateTime.now();
        return toTrends(postRepo.trendingTags(now.minusDays(30), now.plusMinutes(1), now.minusHours(48)), ZentryClock.today().getYear());
    }

    /** Archivo histórico: etiquetas más usadas durante un año */
    public List<TrendingTopicResponse> getTrendingHistory(Integer year) {
        int target = year != null ? year : ZentryClock.today().getYear();
        LocalDateTime start = LocalDate.of(target, 1, 1).atStartOfDay();
        LocalDateTime end = start.plusYears(1);
        // En el histórico no hay "en llamas": hotSince fuera del rango
        return toTrends(postRepo.trendingTags(start, end, end), target);
    }

    private List<TrendingTopicResponse> toTrends(List<Object[]> rows, int year) {
        List<TrendingTopicResponse> result = new ArrayList<>();
        long rank = 1;
        for (Object[] row : rows) {
            String tag = row[0] != null ? row[0].toString().trim() : "";
            if (tag.isEmpty()) continue;
            long total = ((Number) row[1]).longValue();
            long recent = row[2] != null ? ((Number) row[2]).longValue() : 0;
            result.add(TrendingTopicResponse.builder()
                    .id(rank++)
                    .hashtag(tag.startsWith("#") ? tag : "#" + tag)
                    .category(categoryFor(row[3] != null ? row[3].toString() : null))
                    .postsCount(total)
                    .isHot(recent >= 2)
                    .year(year)
                    .updatedAt(LocalDateTime.now())
                    .build());
        }
        return result;
    }

    private static String categoryFor(String contentType) {
        if (contentType == null) return "Arte y creatividad";
        return switch (contentType.toLowerCase()) {
            case "video" -> "Video y animación";
            case "audio" -> "Música y audio";
            case "text" -> "Escritura";
            case "canvas" -> "Arte digital";
            default -> "Arte visual";
        };
    }

    // ------------------------------------------------------------------ Descubrir

    public List<PostResponse> getPopularPosts(int page, String viewerEmail) {
        return postService.getPopular(page, 24, viewerEmail);
    }

    /** Creadores más seguidos que el usuario aún no sigue */
    public List<ProfileResponse> getSuggestedCreators(String viewerEmail) {
        User viewer = viewerEmail != null ? userRepo.findByEmail(viewerEmail).orElse(null) : null;
        List<Integer> ids = new ArrayList<>();
        for (Object[] row : followRepo.topFollowed(40)) ids.add(((Number) row[0]).intValue());
        if (ids.size() < 12) {
            // Pocos seguimientos en la plataforma: completar con creadores recientes con perfil
            userRepo.findAll(PageRequest.of(0, 30)).forEach(u -> { if (!ids.contains(u.getId())) ids.add(u.getId()); });
        }
        if (viewer != null) {
            ids.remove(viewer.getId());
            Set<Integer> already = new HashSet<>(followRepo.followingAmong(viewer.getId(), ids));
            ids.removeIf(already::contains);
        }
        return toProfiles(ids.stream().limit(12).toList(), viewer);
    }

    // ------------------------------------------------------------------ Búsqueda

    public UnifiedSearchResponse unifiedSearch(String query, String viewerEmail) {
        String q = query == null ? "" : query.trim().replaceFirst("^@", "");
        if (q.isEmpty()) {
            return UnifiedSearchResponse.builder()
                    .users(List.of()).arts(List.of()).projects(List.of()).communities(List.of())
                    .trending(getTrendingTopics())
                    .build();
        }
        User viewer = viewerEmail != null ? userRepo.findByEmail(viewerEmail).orElse(null) : null;

        // Creadores por @usuario o nombre visible (nunca por email)
        LinkedHashSet<Integer> userIds = new LinkedHashSet<>();
        userRepo.findTop20ByUsernameContainingIgnoreCase(q).forEach(u -> userIds.add(u.getId()));
        profileRepo.findTop20ByNameContainingIgnoreCase(q).forEach(p -> userIds.add(p.getUserId()));

        String lower = q.toLowerCase();
        List<TrendingTopicResponse> trending = getTrendingTopics().stream()
                .filter(t -> t.getHashtag().toLowerCase().contains(lower) || t.getCategory().toLowerCase().contains(lower))
                .toList();

        return UnifiedSearchResponse.builder()
                .users(toProfiles(userIds.stream().limit(20).toList(), viewer))
                .arts(postService.searchPosts(q, viewerEmail))
                .projects(projectService.searchPublicProjects(q, viewerEmail))
                .communities(communityService.list(q, PageRequest.of(0, 12), viewerEmail).getContent())
                .trending(trending)
                .build();
    }

    /** Perfiles con seguidores, "lo sigo" y cosméticos, en lote */
    private List<ProfileResponse> toProfiles(List<Integer> ids, User viewer) {
        if (ids.isEmpty()) return List.of();
        Map<Integer, User> users = userRepo.findAllById(ids).stream().collect(Collectors.toMap(User::getId, u -> u));
        Map<Integer, Profile> profiles = profileRepo.findByUserIdIn(ids).stream()
                .collect(Collectors.toMap(Profile::getUserId, p -> p, (a, b) -> a));
        Map<Integer, Long> followers = new HashMap<>();
        for (Object[] row : followRepo.countFollowersIn(ids)) followers.put((Integer) row[0], ((Number) row[1]).longValue());
        Set<Integer> following = viewer == null ? Set.of() : new HashSet<>(followRepo.followingAmong(viewer.getId(), ids));
        Map<Integer, CosmeticsResponse> cosmetics = cosmeticsService.forUsers(ids);

        List<ProfileResponse> result = new ArrayList<>();
        for (Integer id : ids) {
            User u = users.get(id);
            if (u == null || u.getHandle() == null) continue;
            Profile p = profiles.getOrDefault(id, new Profile());
            ProfileResponse r = ProfileResponse.builder()
                    .username(u.getHandle())
                    .name(p.getName() != null ? p.getName() : u.getHandle())
                    .discipline(p.getDiscipline())
                    .bio(p.getBio())
                    .avatarUrl(p.getAvatarUrl())
                    .bannerUrl(p.getBannerUrl())
                    .followersCount(followers.getOrDefault(id, 0L).intValue())
                    .followingCount(0)
                    .isFollowing(following.contains(id))
                    .createdAt(p.getCreatedAt())
                    .build();
            r.setCosmetics(cosmetics.get(id));
            result.add(r);
        }
        return result;
    }
}
