package dev.wesley.fieldbooking.dto;

import jakarta.validation.constraints.Size;

public record CreateMyAddressRequest(
        String country,
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        String complement,
        Double latitude,
        Double longitude
) {}
