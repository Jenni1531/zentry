package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Friendship.FriendshipId> {
    List<Friendship> findByUser1(Integer user1);
    List<Friendship> findByUser2(Integer user2);

    @Query("SELECT f FROM Friendship f WHERE f.user1 = :userId OR f.user2 = :userId")
    List<Friendship> findAllFriendshipsForUser(@Param("userId") Integer userId);

    boolean existsByUser1AndUser2(Integer user1, Integer user2);
    void deleteByUser1AndUser2(Integer user1, Integer user2);
=======
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Friendship.FriendshipId> {
   
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
}