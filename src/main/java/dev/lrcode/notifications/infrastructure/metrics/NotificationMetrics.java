package dev.lrcode.notifications.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Getter
public class NotificationMetrics {

    private final Counter notificationsSent;
    private final Counter notificationsFailed;
    private final Counter notificationAttempts;
    private final Counter retriesScheduled;

    public NotificationMetrics(MeterRegistry registry) {

        this.notificationsSent = Counter.builder("notifications.sent")
                .description("Total of notifications success sent")
                .register(registry);

        this.notificationsFailed = Counter.builder("notifications.failed")
                .description("Total of notifications that failed")
                .register(registry);

        this.notificationAttempts = Counter.builder("notifications.attempts")
                .description("Totals shipping attempts")
                .register(registry);

        this.retriesScheduled = Counter.builder("notifications.retries.scheduled")
                .description("Total of retries schedules by backoff")
                .register(registry);
    }
}