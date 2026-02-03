package dev.wesley.fieldbooking.dto;

import java.time.LocalDate;

public record UpdateProfileRequest(
        LocalDate birthDate,
        String avatarUrl,
        String bio
) {}
