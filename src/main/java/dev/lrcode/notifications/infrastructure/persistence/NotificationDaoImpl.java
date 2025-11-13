package dev.lrcode.notifications.infrastructure.persistence;

import dev.lrcode.notifications.domain.enums.NotificationStatus;
import dev.lrcode.notifications.domain.model.Notification;
import dev.lrcode.notifications.domain.repository.NotificationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationDaoImpl implements NotificationDao {

    private final NotificationJpaRepository repository;

    @Override
    public Notification save(Notification n) {
        return repository.save(n);
    }

    @Override
    public Optional<Notification> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<Notification> findForProcessing(List<NotificationStatus> statuses, LocalDateTime now) {
        return repository.findByStatusInAndNextAttemptAtBefore(statuses, now);
    }
}

