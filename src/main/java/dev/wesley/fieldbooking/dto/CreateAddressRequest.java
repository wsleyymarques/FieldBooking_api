package dev.wesley.fieldbooking.dto;

import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateAddressRequest(

        @Size(max = 2)
        String country,

        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,

        Double latitude,
        Double longitude,

        UUID profileId,
        UUID storeId
) {}
