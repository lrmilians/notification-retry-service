package dev.lrcode.notifications.domain.model;

import dev.lrcode.notifications.domain.enums.NotificationChannel;
import dev.lrcode.notifications.domain.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notifications")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(nullable = false)
    private String destination;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private Integer maxAttempts;

    @Column(nullable = false)
    private Integer attemptCount;

    private LocalDateTime nextAttemptAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationAttempt> attempts = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = NotificationStatus.PENDING;
        attemptCount = attemptCount == null ? 0 : attemptCount;
        maxAttempts = maxAttempts == null ? 5 : maxAttempts;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
