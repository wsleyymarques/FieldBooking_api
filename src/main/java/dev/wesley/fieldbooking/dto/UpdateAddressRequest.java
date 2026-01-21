package dev.wesley.fieldbooking.dto;

import jakarta.validation.constraints.Size;

public record UpdateAddressRequest(

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
        Double longitude
) {}
