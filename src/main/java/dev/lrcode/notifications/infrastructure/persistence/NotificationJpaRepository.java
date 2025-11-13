package dev.lrcode.notifications.infrastructure.persistence;

import dev.lrcode.notifications.domain.enums.NotificationStatus;
import dev.lrcode.notifications.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationJpaRepository
        extends JpaRepository<Notification, String> {

    List<Notification> findByStatusInAndNextAttemptAtBefore(
            List<NotificationStatus> statuses,
            LocalDateTime now);
}

