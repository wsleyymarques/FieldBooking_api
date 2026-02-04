package dev.wesley.fieldbooking.dto;

import java.util.UUID;

public record BookingCommandResponse(
        UUID commandId
) { }
