package zentry.back.api.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zentry.back.api.core.models.ProjectChapter;

import java.util.List;

@Repository
public interface ProjectChapterRepository extends JpaRepository<ProjectChapter, Long> {
    List<ProjectChapter> findByProjectIdOrderByPositionAsc(Long projectId);
    long countByProjectId(Long projectId);
    void deleteByProjectId(Long projectId);
}
