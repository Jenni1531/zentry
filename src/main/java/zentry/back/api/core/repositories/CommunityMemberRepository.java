package zentry.back.api.core.repositories;

import zentry.back.api.core.models.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Integer> {
   
}