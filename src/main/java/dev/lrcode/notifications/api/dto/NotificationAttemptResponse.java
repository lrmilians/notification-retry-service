package dev.lrcode.notifications.api.dto;

import java.time.LocalDateTime;

public record NotificationAttemptResponse(
        Long id,
        Integer attemptNumber,
        boolean success,
        String errorMessage,
        LocalDateTime createdAt
) {}