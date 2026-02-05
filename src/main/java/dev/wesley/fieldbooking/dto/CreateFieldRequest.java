package dev.wesley.fieldbooking.dto;

import dev.wesley.fieldbooking.model.Enums.FieldType;
import dev.wesley.fieldbooking.model.Enums.Surface;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateFieldRequest(
        @NotNull
        UUID storeId,

        @NotBlank @Size(max = 120)
        String name,

        @NotNull
        FieldType type,

        @NotNull
        Surface surface,

        @Positive
        BigDecimal pricePerHour,

        @Size(max = 40)
        String sizeLabel,

        Boolean indoor,
        Boolean lighting,
        Boolean lockerRoom
) { }
