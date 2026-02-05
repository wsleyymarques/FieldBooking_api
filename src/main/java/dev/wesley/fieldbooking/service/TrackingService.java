package dev.wesley.fieldbooking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.wesley.fieldbooking.dto.TrackingEventRequest;
import dev.wesley.fieldbooking.error.BadRequestException;
import dev.wesley.fieldbooking.model.TrackingEvent;
import dev.wesley.fieldbooking.repositories.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void trackFrontend(TrackingEventRequest req) {
        save("frontend", req);
    }

    @Transactional
    public void trackBackend(TrackingEventRequest req) {
        save("backend", req);
    }

    private void save(String source, TrackingEventRequest req) {
        if (req == null) {
            throw new BadRequestException("Tracking payload is required");
        }

        TrackingEvent event = new TrackingEvent();
        event.setSource(source);
        event.setEventName(req.eventName());
        event.setDescription(req.description());
        event.setAnonymousId(req.anonymousId());
        event.setClientType(req.clientType());
        event.setUserId(req.userId());
        event.setSessionId(req.sessionId());
        event.setRequestId(req.requestId());
        event.setPath(req.path());
        event.setMethod(req.method());
        event.setStatus(req.status());
        event.setDurationMs(req.durationMs());
        event.setIp(req.ip());
        event.setUserAgent(req.userAgent());
        event.setDevice(req.device());
        event.setPayload(serializePayload(req.payload()));

        repository.save(event);
    }

    private String serializePayload(Object payload) {
        if (payload == null) return null;
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid payload JSON", e);
        }
    }
}
