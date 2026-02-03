package dev.wesley.fieldbooking.dto;

import dev.wesley.fieldbooking.model.Enums.StoreAmenity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateStoreRequest(
        @Size(max = 120)
        String name,

        @Size(max = 18)
        String cnpj,

        @Email @Size(max = 120)
        String contactEmail,

        @Size(max = 20)
        String contactPhone,

        @Size(max = 120)
        String openingHours,

        Set<StoreAmenity> amenities,

        Boolean active
) { }
