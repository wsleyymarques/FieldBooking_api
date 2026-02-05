package dev.wesley.fieldbooking.dto;

import java.util.UUID;

public record TrackingEventRequest(
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
        Object payload
) {
}
