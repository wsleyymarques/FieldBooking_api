package dev.wesley.fieldbooking.error;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldError> errors
) {
    public record FieldError(String field, String message) {
    }
}
