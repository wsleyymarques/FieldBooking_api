package dev.wesley.fieldbooking.dto;

public record RegisterRequest(
        String email,
        String password,
        String phone
) { }
