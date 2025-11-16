package dev.lrcode.notifications.domain.model;

import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void prePersist_shouldInitializeDatesAndDefaults() {
        Notification n = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .destination("test@mail.com")
                .payload("Hello")
                .maxAttempts(3)
                .build();

        // Antes del prePersist
        assertNull(n.getCreatedAt());
        assertNull(n.getUpdatedAt());
        assertNull(n.getStatus());
        assertEquals(0, n.getAttemptCount());

        // Ejecutamos el lifecycle manualmente porque los tests no usan JPA
        n.prePersist();

        assertNotNull(n.getCreatedAt());
        assertNotNull(n.getUpdatedAt());
        assertEquals(NotificationStatus.PENDING, n.getStatus());
        assertEquals(0, n.getAttemptCount());
    }

    @Test
    void setters_shouldWorkCorrectly() {
        Notification n = new Notification();
        n.setDestination("demo@mail.com");
        n.setPayload("Hi!");
        n.setMaxAttempts(5);

        assertEquals("demo@mail.com", n.getDestination());
        assertEquals("Hi!", n.getPayload());
        assertEquals(5, n.getMaxAttempts());
    }
}
