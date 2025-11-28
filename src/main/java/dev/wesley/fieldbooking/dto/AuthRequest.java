package dev.wesley.fieldbooking.dto;

public record AuthRequest(
        String email,
        String password
) { }
