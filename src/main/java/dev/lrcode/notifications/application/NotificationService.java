package dev.lrcode.notifications.application;

import dev.lrcode.notifications.api.dto.*;
import dev.lrcode.notifications.application.mapper.NotificationMapper;
import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;
import dev.lrcode.notifications.domain.model.Notification;
import dev.lrcode.notifications.domain.model.NotificationAttempt;
import dev.lrcode.notifications.domain.repository.NotificationAttemptDao;
import dev.lrcode.notifications.domain.repository.NotificationDao;
import dev.lrcode.notifications.domain.repository.NotificationRepository;
import dev.lrcode.notifications.infrastructure.metrics.NotificationMetrics;
import dev.lrcode.notifications.infrastructure.provider.NotificationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final NotificationAttemptDao attemptDao;

    private final NotificationMetrics metrics;

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

        int nextAttempt = n.getAttemptCount() + 1;

        try {
            boolean success = provider.send(n);

            // Save attempt
            attemptDao.save(NotificationAttempt.builder()
                    .attemptNumber(nextAttempt)
                    .success(success)
                    .errorMessage(success ? null : "Unknow error or provider fail.")
                    .notification(n)
                    .build()
            );

            metrics.getNotificationAttempts().increment();   // We always count the attempt

            if (success) {
                n.setStatus(NotificationStatus.SENT);
                n.setAttemptCount(nextAttempt);
                n.setNextAttemptAt(null);
                dao.save(n);

                metrics.getNotificationsSent().increment();  // Count successful sends
                return;
            }

            metrics.getNotificationsFailed().increment();
            handleFailure(n, nextAttempt);

        } catch (Exception ex) {

            attemptDao.save(NotificationAttempt.builder()
                    .attemptNumber(nextAttempt)
                    .success(false)
                    .errorMessage(ex.getMessage())
                    .notification(n)
                    .build()
            );

            metrics.getNotificationsFailed().increment();
            handleFailure(n, nextAttempt);
        }
    }

    private void handleFailure(Notification n, int attempt) {
        if (attempt >= n.getMaxAttempts()) {
            n.setStatus(NotificationStatus.FAILED);
            n.setAttemptCount(attempt);
            n.setNextAttemptAt(null);
            dao.save(n);
            return;
        }

        // backoff exponencial
        long minutes = (long) Math.pow(2, attempt - 1);

        n.setAttemptCount(attempt);
        n.setStatus(NotificationStatus.RETRY_PENDING);
        n.setNextAttemptAt(LocalDateTime.now().plusMinutes(minutes));

        dao.save(n);
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

    public PagedResponse<NotificationResponse> listPaged(
            NotificationStatus status,
            NotificationChannel channel,
            Pageable pageable) {

        Page<Notification> page =
                dao.findByFiltersPaged(status, channel, pageable);

        List<NotificationResponse> content =
                page.getContent().stream()
                        .map(mapper::toResponse)
                        .toList();

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    public List<NotificationAttemptResponse> listAttempts(String notificationId) {
        return dao.findById(notificationId)
                .map(Notification::getAttempts)
                .orElse(List.of())
                .stream()
                .map(mapper::toAttemptResponse)
                .toList();
    }



}
