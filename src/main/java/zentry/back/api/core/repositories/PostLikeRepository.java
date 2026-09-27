package zentry.back.api.core.repositories;

import zentry.back.api.core.models.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLike.PostLikeId> {
    boolean existsByPostIdAndUserId(Integer postId, Integer userId);
    Optional<PostLike> findByPostIdAndUserId(Integer postId, Integer userId);
    long countByPostId(Integer postId);
    void deleteByPostIdAndUserId(Integer postId, Integer userId);
    void deleteByPostId(Integer postId);
    List<PostLike> findByUserIdOrderByCreatedAtDesc(Integer userId);

    @Query("SELECT l.reactionType, COUNT(l) FROM PostLike l WHERE l.postId = :postId GROUP BY l.reactionType")
    List<Object[]> countByReactionType(@Param("postId") Integer postId);

    /** Filas [postId, reactionType, count] para varias publicaciones en una sola consulta */
    @Query("SELECT l.postId, l.reactionType, COUNT(l) FROM PostLike l WHERE l.postId IN :postIds GROUP BY l.postId, l.reactionType")
    List<Object[]> countByPostIdsAndReactionType(@Param("postIds") java.util.Collection<Integer> postIds);

    List<PostLike> findByUserIdAndPostIdIn(Integer userId, java.util.Collection<Integer> postIds);
}
