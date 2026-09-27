package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("coreNotificationRepository")
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    Optional<Notification> findByIdAndUserId(Integer id, Integer userId);
    void deleteByUserId(Integer userId);
    long countByUserIdAndReadFalse(Integer userId);
    Optional<Notification> findFirstByUserIdAndTypeAndRelatedIdAndReadFalse(Integer userId, String type, Integer relatedId);

    @Modifying
    @Query("UPDATE CoreNotification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsRead(@Param("userId") Integer userId);
}
