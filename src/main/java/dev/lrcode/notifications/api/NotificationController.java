package dev.lrcode.notifications.api;

import dev.lrcode.notifications.api.dto.*;
import dev.lrcode.notifications.application.NotificationService;
import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<NotificationResponse>> listPaged(
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationChannel channel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        PagedResponse<NotificationResponse> response =
                service.listPaged(status, channel, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity<List<NotificationAttemptResponse>> attempts(@PathVariable String id) {

        return service.findById(id)
                .map(n -> ResponseEntity.ok(service.listAttempts(id)))
                .orElse(ResponseEntity.notFound().build());
    }


}

