package dev.wesley.fieldbooking.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TrackingEventResponse(
        UUID id,
        String source,
        String eventName,
        String description,
        String anonymousId,
        String clientType,
        UUID userId,
        String sessionId,
        String requestId,
        String path,
        String method,
        Integer status,
        Long durationMs,
        String ip,
        String userAgent,
        String device,
        String payload,
        OffsetDateTime createdAt
) {
}
