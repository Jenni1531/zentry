package zentry.back.api.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zentry.back.api.core.models.Project;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByCreatedByOrderByUpdatedAtDesc(String createdBy);
    List<Project> findByCreatedByIn(java.util.Collection<String> createdBy);

    /** Proyectos públicos para Explorar (más recientes primero) */
    org.springframework.data.domain.Page<Project> findByVisibilityOrderByUpdatedAtDesc(String visibility, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.visibility = 'public' AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Project> searchPublic(@Param("query") String query);

    @Query("SELECT p FROM Project p WHERE p.createdBy = :createdBy AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Project> searchProjectsByUser(@Param("query") String query, @Param("createdBy") String createdBy);
}
