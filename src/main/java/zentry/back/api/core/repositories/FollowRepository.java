package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Follow.FollowId> {
    boolean existsByFollowerAndFollowing(Integer follower, Integer following);
    long countByFollowing(Integer following);
    long countByFollower(Integer follower);
    void deleteByFollowerAndFollowing(Integer follower, Integer following);
    java.util.List<Follow> findByFollowing(Integer following);
    java.util.List<Follow> findByFollower(Integer follower);

    /** Filas [userId, seguidores] de los más seguidos */
    @org.springframework.data.jpa.repository.Query(value = "SELECT f.following, COUNT(*) AS c FROM zentry_core.follows f GROUP BY f.following ORDER BY c DESC LIMIT :limit", nativeQuery = true)
    java.util.List<Object[]> topFollowed(@org.springframework.data.repository.query.Param("limit") int limit);

    /** Filas [userId, seguidores] para varios usuarios */
    @org.springframework.data.jpa.repository.Query("SELECT f.following, COUNT(f) FROM Follow f WHERE f.following IN :ids GROUP BY f.following")
    java.util.List<Object[]> countFollowersIn(@org.springframework.data.repository.query.Param("ids") java.util.Collection<Integer> ids);

    @org.springframework.data.jpa.repository.Query("SELECT f.following FROM Follow f WHERE f.follower = :follower AND f.following IN :ids")
    java.util.List<Integer> followingAmong(@org.springframework.data.repository.query.Param("follower") Integer follower,
                                           @org.springframework.data.repository.query.Param("ids") java.util.Collection<Integer> ids);
}
