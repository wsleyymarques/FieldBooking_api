package dev.wesley.fieldbooking.dto;

public record UploadResult(
        String bucket,
        String path,
        String publicUrl
) {}
