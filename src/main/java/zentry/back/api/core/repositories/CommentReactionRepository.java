package zentry.back.api.core.repositories;

import zentry.back.api.core.models.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Integer> {
    long countByCommentId(Integer commentId);
    boolean existsByCommentIdAndUserId(Integer commentId, Integer userId);
    Optional<CommentReaction> findByCommentIdAndUserId(Integer commentId, Integer userId);
    void deleteByCommentIdAndUserId(Integer commentId, Integer userId);
    void deleteByCommentId(Integer commentId);
    void deleteByCommentIdIn(java.util.List<Integer> commentIds);

    @org.springframework.data.jpa.repository.Query("SELECT r.commentId, COUNT(r) FROM CommentReaction r WHERE r.commentId IN :ids GROUP BY r.commentId")
    java.util.List<Object[]> countByCommentIds(@org.springframework.data.repository.query.Param("ids") java.util.Collection<Integer> ids);

    java.util.List<CommentReaction> findByUserIdAndCommentIdIn(Integer userId, java.util.Collection<Integer> commentIds);
}
