package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** Feed general: excluye publicaciones de comunidades privadas */
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Post p WHERE p.communityId IS NULL OR p.communityId NOT IN (SELECT c.id FROM Community c WHERE c.privacy = 'private') ORDER BY p.createdAt DESC")
    Page<Post> findFeed(Pageable pageable);

    /** Populares: más reacciones desde una fecha (sin comunidades privadas) */
    @org.springframework.data.jpa.repository.Query(value = "SELECT p.* FROM zentry_core.posts p "
            + "LEFT JOIN (SELECT post_id, COUNT(*) AS c FROM zentry_core.post_likes GROUP BY post_id) l ON l.post_id = p.id "
            + "WHERE p.created_at >= :since AND (p.community_id IS NULL OR p.community_id NOT IN (SELECT c.id FROM zentry_core.communities c WHERE c.privacy = 'private')) "
            + "ORDER BY COALESCE(l.c, 0) DESC, p.created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Post> findPopularSince(@org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since,
                                @org.springframework.data.repository.query.Param("limit") int limit,
                                @org.springframework.data.repository.query.Param("offset") int offset);

    /** Búsqueda de obras por título, texto o etiqueta (sin comunidades privadas) */
    @org.springframework.data.jpa.repository.Query(value = "SELECT DISTINCT p.* FROM zentry_core.posts p "
            + "LEFT JOIN zentry_core.post_tools t ON t.post_id = p.id "
            + "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.contenido) LIKE LOWER(CONCAT('%', :q, '%')) "
            + "OR LOWER(t.tool) LIKE LOWER(CONCAT('%', :q, '%'))) AND (p.community_id IS NULL OR p.community_id NOT IN (SELECT c.id FROM zentry_core.communities c WHERE c.privacy = 'private')) "
            + "ORDER BY p.created_at DESC LIMIT 40", nativeQuery = true)
    List<Post> searchPublic(@org.springframework.data.repository.query.Param("q") String q);

    /**
     * Tendencias reales: etiquetas más usadas en un rango de fechas.
     * Filas: [tag, total, recientes (desde hotSince), tipo de contenido más frecuente]
     */
    @org.springframework.data.jpa.repository.Query(value = "SELECT LOWER(t.tool) AS tag, COUNT(*) AS total, "
            + "SUM(CASE WHEN p.created_at >= :hotSince THEN 1 ELSE 0 END) AS recent, "
            + "MODE() WITHIN GROUP (ORDER BY p.content_type) AS ctype "
            + "FROM zentry_core.post_tools t JOIN zentry_core.posts p ON p.id = t.post_id "
            + "WHERE p.created_at >= :since AND p.created_at < :until AND t.tool IS NOT NULL AND TRIM(t.tool) <> '' "
            + "AND LOWER(TRIM(t.tool)) NOT IN ('#zentry', '#creatividad', 'zentry', 'creatividad') "
            + "AND (p.community_id IS NULL OR p.community_id NOT IN (SELECT c.id FROM zentry_core.communities c WHERE c.privacy = 'private')) "
            + "GROUP BY LOWER(t.tool) ORDER BY total DESC LIMIT 20", nativeQuery = true)
    List<Object[]> trendingTags(@org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since,
                                @org.springframework.data.repository.query.Param("until") java.time.LocalDateTime until,
                                @org.springframework.data.repository.query.Param("hotSince") java.time.LocalDateTime hotSince);
    List<Post> findByTitleContainingIgnoreCaseOrContenidoContainingIgnoreCaseOrderByCreatedAtDesc(String title, String contenido);
    List<Post> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Post> findByUserIdOrderByUpdatedAtDesc(Integer userId);
    Page<Post> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    Optional<Post> findByIdAndUserId(Integer id, Integer userId);
    long countByUserId(Integer userId);
    Page<Post> findByCommunityIdOrderByCreatedAtDesc(Integer communityId, Pageable pageable);
    List<Post> findByCommunityId(Integer communityId);
}