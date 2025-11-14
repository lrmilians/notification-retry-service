package dev.lrcode.notifications.infrastructure.persistence;

import dev.lrcode.notifications.domain.model.NotificationAttempt;
import dev.lrcode.notifications.domain.repository.NotificationAttemptDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationAttemptDaoImpl implements NotificationAttemptDao {

    private final NotificationAttemptJpaRepository repository;

    @Override
    public NotificationAttempt save(NotificationAttempt attempt) {
        return repository.save(attempt);
    }
}