package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findByTitleContainingIgnoreCaseOrContenidoContainingIgnoreCaseOrderByCreatedAtDesc(String title, String contenido);
    List<Post> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Post> findByUserIdOrderByUpdatedAtDesc(Integer userId);
    Page<Post> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    Optional<Post> findByIdAndUserId(Integer id, Integer userId);
    long countByUserId(Integer userId);
=======
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    java.util.List<Post> findByTitleContainingIgnoreCaseOrContenidoContainingIgnoreCaseOrderByCreatedAtDesc(String title, String contenido);
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
}