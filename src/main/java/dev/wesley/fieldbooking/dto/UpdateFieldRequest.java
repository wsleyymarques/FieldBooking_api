package dev.wesley.fieldbooking.dto;

import dev.wesley.fieldbooking.model.Enums.FieldStatus;
import dev.wesley.fieldbooking.model.Enums.FieldType;
import dev.wesley.fieldbooking.model.Enums.Surface;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateFieldRequest(
        @Size(max = 120)
        String name,

        FieldType type,
        Surface surface,

        @Positive
        BigDecimal pricePerHour,

        @Size(max = 40)
        String sizeLabel,

        Boolean indoor,
        Boolean lighting,
        Boolean lockerRoom,

        FieldStatus status
) { }
