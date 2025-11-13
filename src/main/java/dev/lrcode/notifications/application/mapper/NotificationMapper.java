package dev.lrcode.notifications.application.mapper;

import dev.lrcode.notifications.api.dto.NotificationRequest;
import dev.lrcode.notifications.api.dto.NotificationResponse;
import dev.lrcode.notifications.domain.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {

    // DTO → ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "attemptCount", ignore = true)
    @Mapping(target = "nextAttemptAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Notification toEntity(NotificationRequest req);

    // ENTITY → DTO
    NotificationResponse toResponse(Notification entity);
}

