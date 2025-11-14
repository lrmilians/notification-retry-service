package dev.lrcode.notifications.domain.repository;

import dev.lrcode.notifications.domain.model.NotificationAttempt;

public interface NotificationAttemptDao {

    NotificationAttempt save(NotificationAttempt attempt);
}
