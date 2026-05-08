package zentry.back.api.core.repositories;

import zentry.back.api.core.models.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumThreadRepository extends JpaRepository<ForumThread, Integer> {
   
}