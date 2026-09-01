package zentry.back.api.core.repositories;

import zentry.back.api.core.models.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
<<<<<<< HEAD
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    List<FriendRequest> findByUser2(Integer user2);
    List<FriendRequest> findByUser1(Integer user1);
    boolean existsByUser1AndUser2(Integer user1, Integer user2);
    Optional<FriendRequest> findByUser1AndUser2(Integer user1, Integer user2);
    void deleteByUser1AndUser2(Integer user1, Integer user2);
=======

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
   
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
}