package dev.lrcode.notifications.infrastructure.provider;

import dev.lrcode.notifications.domain.model.Notification;

public interface NotificationProvider {
    boolean send(Notification n);
}

