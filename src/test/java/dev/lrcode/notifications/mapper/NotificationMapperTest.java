package dev.lrcode.notifications.mapper;

import dev.lrcode.notifications.api.dto.NotificationRequest;
import dev.lrcode.notifications.api.dto.NotificationResponse;
import dev.lrcode.notifications.application.mapper.NotificationMapper;
import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;
import dev.lrcode.notifications.domain.model.Notification;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMapperTest {

    // Obtiene instancia del mapper sin usar Spring
    private final NotificationMapper mapper =
            Mappers.getMapper(NotificationMapper.class);

    @Test
    void shouldMapFromRequestToEntity() {

        NotificationRequest req = new NotificationRequest(
                NotificationChannel.EMAIL,
                "test@mail.com",
                "Hello!",
                3
        );

        Notification entity = mapper.toEntity(req);

        assertNotNull(entity);
        assertEquals("test@mail.com", entity.getDestination());
        assertEquals("Hello!", entity.getPayload());
        assertEquals(3, entity.getMaxAttempts());
        assertEquals(NotificationChannel.EMAIL, entity.getChannel());

        // Campos ignorados por MapStruct deben ser null (antes de prePersist)
        assertNull(entity.getId());
        assertNull(entity.getStatus());
        assertNull(entity.getNextAttemptAt());
    }

    @Test
    void shouldMapFromEntityToResponse() {
        Notification entity = Notification.builder()
                .id("abc123")
                .channel(NotificationChannel.SMS)
                .destination("098765432")
                .payload("Code 1234")
                .status(NotificationStatus.SENT)
                .attemptCount(2)
                .maxAttempts(5)
                .build();

        NotificationResponse res = mapper.toResponse(entity);

        assertNotNull(res);
        assertEquals("abc123", res.id());
        assertEquals(NotificationChannel.SMS, res.channel());
        assertEquals("098765432", res.destination());
        assertEquals("Code 1234", res.payload());
        assertEquals(NotificationStatus.SENT, res.status());
        assertEquals(2, res.attemptCount());
        assertEquals(5, res.maxAttempts());
    }
}
