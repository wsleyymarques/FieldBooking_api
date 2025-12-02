package dev.wesley.fieldbooking.dto;

public record RegisterRequest(
        String firstName,
        String email,
        String password,
        String phone
) { }
