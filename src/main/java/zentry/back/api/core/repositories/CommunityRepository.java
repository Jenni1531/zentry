package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Integer> {
   
}