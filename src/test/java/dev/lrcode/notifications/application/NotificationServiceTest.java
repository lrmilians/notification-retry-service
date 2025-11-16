package dev.lrcode.notifications.application;

import dev.lrcode.notifications.api.dto.NotificationRequest;
import dev.lrcode.notifications.api.dto.NotificationResponse;
import dev.lrcode.notifications.application.mapper.NotificationMapper;
import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;
import dev.lrcode.notifications.domain.model.Notification;
import dev.lrcode.notifications.domain.repository.NotificationDao;
import dev.lrcode.notifications.domain.repository.NotificationRepository;
import dev.lrcode.notifications.infrastructure.metrics.NotificationMetrics;
import dev.lrcode.notifications.domain.repository.NotificationAttemptDao;
import dev.lrcode.notifications.infrastructure.provider.NotificationProvider;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private NotificationDao dao;
    private NotificationProvider provider;
    private NotificationAttemptDao attemptDao;
    private NotificationMapper mapper;
    private NotificationMetrics metrics;

    private NotificationService service;
    private NotificationRepository repository;

    @BeforeEach
    void setUp() {
        dao = mock(NotificationDao.class);
        repository = mock(NotificationRepository.class);
        provider = mock(NotificationProvider.class);
        attemptDao = mock(NotificationAttemptDao.class);
        mapper = Mockito.spy(Mappers.getMapper(NotificationMapper.class));
        metrics = mock(NotificationMetrics.class);

        metrics = mock(NotificationMetrics.class);
        Counter failed = mock(Counter.class);
        Counter attempts = mock(Counter.class);
        Counter sent = mock(Counter.class);
        Counter retries = mock(Counter.class);

        when(metrics.getNotificationsFailed()).thenReturn(failed);
        when(metrics.getNotificationAttempts()).thenReturn(attempts);
        when(metrics.getNotificationsSent()).thenReturn(sent);
        when(metrics.getRetriesScheduled()).thenReturn(retries);

        service = new NotificationService(
                repository, provider, dao, mapper, attemptDao, metrics
        );
    }

    @Test
    void createNotification_ok() {
        NotificationRequest req = new NotificationRequest(
                NotificationChannel.EMAIL,
                "test@test.com",
                "Hello",
                3
        );

        Notification saved = Notification.builder()
                .id("abc123")
                .channel(NotificationChannel.EMAIL)
                .destination("test@test.com")
                .payload("Hello")
                .maxAttempts(3)
                .attemptCount(0)
                .nextAttemptAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(dao.save(any(Notification.class))).thenReturn(saved);

        NotificationResponse res = service.create(req);

        assertNotNull(res);
        assertEquals("abc123", res.id());
        assertEquals(NotificationChannel.EMAIL, res.channel());

        verify(dao, times(1)).save(any());
    }

    @Test
    void processOne_success() {

        // --------- Arrange ----------
        Notification n = Notification.builder()
                .id("OK-123")
                .status(NotificationStatus.PENDING)
                .attemptCount(0)
                .maxAttempts(3)
                .nextAttemptAt(LocalDateTime.now().minusMinutes(5))
                .build();


        when(repository.findByStatusInAndNextAttemptAtBefore(any(), any()))
                .thenReturn(List.of(n));


        when(provider.send(n)).thenReturn(true);


        Counter attempts = mock(Counter.class);
        Counter sent = mock(Counter.class);

        when(metrics.getNotificationAttempts()).thenReturn(attempts);
        when(metrics.getNotificationsSent()).thenReturn(sent);

        // --------- Act ----------
        service.processPendingNotifications();

        // --------- Assert ----------
        verify(attempts, times(1)).increment();
        verify(sent, times(1)).increment();
        verify(dao, atLeast(1)).save(any());
    }

    @Test
    void processOne_failure_retry() {

        Notification n = Notification.builder()
                .id("2")
                .attemptCount(0)
                .maxAttempts(3)
                .nextAttemptAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(provider.send(n)).thenReturn(false);

        // Add the notification to the repository output so that it can be processed
        when(repository.findByStatusInAndNextAttemptAtBefore(any(), any()))
                .thenReturn(List.of(n));

        service.processPendingNotifications();

        verify(metrics.getNotificationsFailed(), times(1)).increment();
    }

    @Test
    void shouldSetStatusFailed_whenMaxAttemptsReached() {

        Notification n = Notification.builder()
                .id("3")
                .attemptCount(2)
                .maxAttempts(2)
                .nextAttemptAt(LocalDateTime.now().minusMinutes(1))
                .build();

        // Simulate that the supplier fails
        when(provider.send(n)).thenReturn(false);

        // Add this notification as pending in the DAO
        when(repository.findByStatusInAndNextAttemptAtBefore(
                any(), any())
        ).thenReturn(List.of(n));

        service.processPendingNotifications();

        assertEquals(NotificationStatus.FAILED, n.getStatus());
        assertNull(n.getNextAttemptAt());
    }


    @Test
    void mapper_shouldMapRequestToEntity() {
        NotificationRequest req = new NotificationRequest(
                NotificationChannel.EMAIL,
                "john@mail.com",
                "Hi John",
                5
        );

        NotificationMapper mapper = Mockito.spy(Mappers.getMapper(NotificationMapper.class));

        Notification entity = mapper.toEntity(req);

        assertEquals("john@mail.com", entity.getDestination());
        assertEquals(5, entity.getMaxAttempts());
    }

    @Test
    void processOne_shouldSaveAttempt() {

        Notification n = Notification.builder()
                .id("5")
                .status(NotificationStatus.PENDING)
                .attemptCount(0)
                .maxAttempts(3)
                .nextAttemptAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(provider.send(n)).thenReturn(false);

        when(repository.findByStatusInAndNextAttemptAtBefore(any(), any()))
                .thenReturn(List.of(n));

        service.processPendingNotifications();

        verify(attemptDao, times(1)).save(any());
    }

    @Test
    void forceRetry_shouldUpdateStatus() {

        Notification n = Notification.builder()
                .id("10")
                .status(NotificationStatus.PENDING)
                .nextAttemptAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(repository.findById("10")).thenReturn(Optional.of(n));

        boolean result = service.forceRetry("10");

        assertTrue(result);
        assertEquals(NotificationStatus.RETRY_PENDING, n.getStatus());
    }

}