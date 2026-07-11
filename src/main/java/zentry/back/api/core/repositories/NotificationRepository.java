package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("coreNotificationRepository")
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
   
}