package dev.lrcode.notifications.api.dto;

import dev.lrcode.notifications.domain.enums.NotificationChannel;

public record NotificationRequest(
        NotificationChannel channel,
        String destination,
        String payload,
        Integer maxAttempts
) {}

