package dev.lrcode.notifications.domain.repository;

import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;
import dev.lrcode.notifications.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationDao {

    Notification save(Notification n);

    Optional<Notification> findById(String id);

    List<Notification> findForProcessing(
            List<NotificationStatus> statuses,
            LocalDateTime now
    );

    Page<Notification> findByFiltersPaged(
            NotificationStatus status,
            NotificationChannel channel,
            Pageable pageable
    );
}

