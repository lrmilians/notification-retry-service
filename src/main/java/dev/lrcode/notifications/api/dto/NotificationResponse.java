package dev.lrcode.notifications.api.dto;

import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;

import java.time.LocalDateTime;

public record NotificationResponse(
        String id,
        NotificationChannel channel,
        String destination,
        String payload,
        NotificationStatus status,
        Integer attemptCount,
        Integer maxAttempts,
        LocalDateTime nextAttemptAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

