package dev.lrcode.notifications.api.dto;

import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;

public record NotificationFilterRequest(
        NotificationStatus status,
        NotificationChannel channel
) {}

