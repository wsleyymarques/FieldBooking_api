package dev.wesley.fieldbooking.dto;

import java.time.LocalDate;

public record CreateProfileRequest(
        LocalDate birthDate,
        String avatarUrl,
        String bio
) {}
