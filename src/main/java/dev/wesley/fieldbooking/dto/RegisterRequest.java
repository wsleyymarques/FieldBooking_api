package dev.wesley.fieldbooking.dto;

public record RegisterRequest(
        String name,
        String firstName,
        String lastName,
        String email,
        String password,
        String phone
) { }
