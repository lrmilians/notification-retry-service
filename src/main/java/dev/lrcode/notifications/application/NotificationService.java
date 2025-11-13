package dev.lrcode.notifications.application;

import dev.lrcode.notifications.api.dto.NotificationRequest;
import dev.lrcode.notifications.api.dto.NotificationResponse;
import dev.lrcode.notifications.application.mapper.NotificationMapper;
import dev.lrcode.notifications.domain.enums.NotificationStatus;
import dev.lrcode.notifications.domain.model.Notification;
import dev.lrcode.notifications.domain.repository.NotificationDao;
import dev.lrcode.notifications.domain.repository.NotificationRepository;
import dev.lrcode.notifications.infrastructure.provider.NotificationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationProvider provider;

    private final NotificationDao dao;
    private final NotificationMapper mapper;

    public NotificationResponse create(NotificationRequest req) {
        Notification n = mapper.toEntity(req);
        Notification saved = dao.save(n);

        return mapper.toResponse(saved);
    }

    public Optional<NotificationResponse> findById(String id) {
        return dao.findById(id).map(mapper::toResponse);
    }


    /**
     * Process all notifications PENDING o RETRY_PENDING
     */
    public int processPendingNotifications() {

        List<Notification> list =
                repository.findByStatusInAndNextAttemptAtBefore(
                        List.of(NotificationStatus.PENDING, NotificationStatus.RETRY_PENDING),
                        LocalDateTime.now()
                );

        if (list.isEmpty()) return 0;

        list.forEach(this::processOne);

        return list.size();
    }

    private void processOne(Notification n) {
        try {
            log.info("▶ Trying to send notification {}...", n.getId());

            boolean success = provider.send(n);

            if (success) {
                n.setStatus(NotificationStatus.SENT);
                n.setAttemptCount(n.getAttemptCount() + 1);
                n.setNextAttemptAt(null);
                repository.save(n);
                log.info("✔ Notification {} sent", n.getId());
            } else {
                handleFailure(n);
            }

        } catch (Exception ex) {
            log.error("❌ Sending error {}: {}", n.getId(), ex.getMessage());
            handleFailure(n);
        }
    }

    private void handleFailure(Notification n) {
        int attempt = n.getAttemptCount() + 1;

        if (attempt >= n.getMaxAttempts()) {
            n.setStatus(NotificationStatus.FAILED);
            n.setAttemptCount(attempt);
            n.setNextAttemptAt(null);
            repository.save(n);

            log.warn("💀 Notification {} marked as FAILED (maximum attempts achieved)", n.getId());
            return;
        }

        // RETRY — backoff exponencial
        long minutes = (long) Math.pow(2, attempt - 1);

        n.setAttemptCount(attempt);
        n.setStatus(NotificationStatus.RETRY_PENDING);
        n.setNextAttemptAt(LocalDateTime.now().plusMinutes(minutes));

        repository.save(n);

        log.warn("⏱ Trying {} in {} minutes (attempt {})",
                n.getId(), minutes, attempt);
    }

    public boolean forceRetry(String id) {
        Optional<Notification> opt = repository.findById(id);

        if (opt.isEmpty()) return false;

        Notification n = opt.get();

        if (n.getStatus() == NotificationStatus.SENT) {
            log.warn("⚠ The notification {} has already been sent. Retry ignored.", id);
            return false;
        }

        log.info("🔄 Forcing retry immediate for {}", id);

        n.setNextAttemptAt(LocalDateTime.now());
        n.setStatus(NotificationStatus.RETRY_PENDING);

        repository.save(n);

        return true;
    }

}

