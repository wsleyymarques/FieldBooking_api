package dev.wesley.fieldbooking.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingCommand(
        UUID commandId,
        UUID fieldId,
        UUID userId,
        OffsetDateTime startAt,
        OffsetDateTime endAt
) { }
