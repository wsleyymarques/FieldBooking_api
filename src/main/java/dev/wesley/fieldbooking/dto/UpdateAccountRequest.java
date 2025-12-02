package dev.wesley.fieldbooking.dto;

public record UpdateAccountRequest(
        String email,
        String phone,
        String currentPassword,
        String newPassword
) { }
