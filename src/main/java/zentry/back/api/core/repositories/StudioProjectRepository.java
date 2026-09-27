package zentry.back.api.core.repositories;

import zentry.back.api.core.models.ContentType;
import zentry.back.api.core.models.StudioProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudioProjectRepository extends JpaRepository<StudioProject, Integer> {
    List<StudioProject> findByOwnerUsernameOrderByLastEditedAtDesc(String ownerUsername);
    List<StudioProject> findByOwnerUsernameAndTypeOrderByLastEditedAtDesc(String ownerUsername, ContentType type);
    List<StudioProject> findByOwnerUsernameInOrderByLastEditedAtDesc(Collection<String> ownerUsernames);
    List<StudioProject> findByOwnerUsernameInAndTypeOrderByLastEditedAtDesc(Collection<String> ownerUsernames, ContentType type);
    List<StudioProject> findByPostId(Integer postId);
    List<StudioProject> findByProjectId(Long projectId);
    Optional<StudioProject> findByIdAndOwnerUsername(Integer id, String ownerUsername);
}
