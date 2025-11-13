package dev.lrcode.notifications.api;

import dev.lrcode.notifications.api.dto.NotificationRequest;
import dev.lrcode.notifications.api.dto.NotificationResponse;
import dev.lrcode.notifications.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@RequestBody NotificationRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/retry-now")
    public ResponseEntity<?> retryNow(@PathVariable String id) {

        boolean ok = service.forceRetry(id);

        if (!ok) {
            return ResponseEntity.badRequest()
                    .body("The notification " + id + " can´t be reprocessed");
        }

        return ResponseEntity.ok("Retry scheduled for immediate execution.");
    }
}

