package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    java.util.List<Post> findByTitleContainingIgnoreCaseOrContenidoContainingIgnoreCaseOrderByCreatedAtDesc(String title, String contenido);
}