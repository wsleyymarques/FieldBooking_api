package dev.wesley.fieldbooking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tracking_event")
@Getter
@Setter
@NoArgsConstructor
public class TrackingEvent {
    @Id
    private UUID id;

    private String source;
    private String eventName;
    private String description;

    @Column(name = "anonymous_id")
    private String anonymousId;

    @Column(name = "client_type")
    private String clientType;

    private UUID userId;
    private String sessionId;
    private String requestId;

    private String path;
    private String method;
    private Integer status;
    private Long durationMs;

    private String ip;
    private String userAgent;
    private String device;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(columnDefinition = "jsonb")
    private String payload;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
