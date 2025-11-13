package dev.lrcode.notifications.infrastructure.scheduler;

import dev.lrcode.notifications.application.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    // Run each 5 seconds
    @Scheduled(fixedDelayString = "5000")
    public void processPendingNotifications() {
        log.info("⏳ Running notification sending schedule...");

        int processed = notificationService.processPendingNotifications();

        log.info("📬 Notifications processed in this cycle : {}", processed);
    }
}