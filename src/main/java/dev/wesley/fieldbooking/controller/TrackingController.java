package dev.wesley.fieldbooking.controller;

import dev.wesley.fieldbooking.dto.TrackingEventRequest;
import dev.wesley.fieldbooking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @PostMapping("/event")
    public ResponseEntity<Void> trackEvent(@RequestBody TrackingEventRequest req) {
        trackingService.trackFrontend(req);
        return ResponseEntity.accepted().build();
    }
}
