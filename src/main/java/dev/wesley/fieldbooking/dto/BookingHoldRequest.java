package dev.wesley.fieldbooking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingHoldRequest(
        @NotNull
        UUID fieldId,

        @NotNull @Future
        OffsetDateTime startAt,

        @NotNull @Future
        OffsetDateTime endAt
) { }
