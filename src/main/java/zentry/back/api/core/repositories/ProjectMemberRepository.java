package zentry.back.api.core.repositories;

import zentry.back.api.core.models.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMember.ProjectMemberId> {
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByUsername(String username);
    Optional<ProjectMember> findByProjectIdAndUsername(Long projectId, String username);
    boolean existsByProjectIdAndUsername(Long projectId, String username);
    boolean existsByProjectIdAndUsernameIn(Long projectId, java.util.Collection<String> usernames);
    List<ProjectMember> findByUsernameIn(java.util.Collection<String> usernames);
    long countByProjectId(Long projectId);
    void deleteByProjectId(Long projectId);
}
