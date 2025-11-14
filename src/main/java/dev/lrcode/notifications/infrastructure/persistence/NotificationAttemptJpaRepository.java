package dev.lrcode.notifications.infrastructure.persistence;

import dev.lrcode.notifications.domain.model.NotificationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationAttemptJpaRepository
        extends JpaRepository<NotificationAttempt, Long> {
}