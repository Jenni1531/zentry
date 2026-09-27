package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Integer postId);
    long countByPostId(Integer postId);

    /** Filas [postId, count] para varias publicaciones en una sola consulta */
    @org.springframework.data.jpa.repository.Query("SELECT c.postId, COUNT(c) FROM Comment c WHERE c.postId IN :postIds GROUP BY c.postId")
    List<Object[]> countByPostIds(@org.springframework.data.repository.query.Param("postIds") java.util.Collection<Integer> postIds);
    void deleteByPostId(Integer postId);
}
