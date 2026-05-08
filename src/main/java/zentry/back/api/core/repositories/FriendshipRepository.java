package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
   
}