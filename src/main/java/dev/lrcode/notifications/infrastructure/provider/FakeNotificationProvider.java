package dev.lrcode.notifications.infrastructure.provider;

import dev.lrcode.notifications.domain.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class FakeNotificationProvider implements NotificationProvider {

    private final Random random = new Random();

    @Override
    public boolean send(Notification n) {

        // 70% éxito, 30% falla (para simular comportamiento real)
        boolean success = random.nextInt(100) < 70;

        log.info("📨 Simulando envío a {} → {}",
                n.getDestination(),
                success ? "OK" : "ERROR");

        return success;
    }
}

