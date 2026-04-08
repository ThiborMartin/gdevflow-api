package br.com.gdevflow.api.gdevflow_api.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors) {

    public static ApiErrorResponse of(int status, String message) {
        return new ApiErrorResponse(status, message, LocalDateTime.now(), null);
    }

    public static ApiErrorResponse of(int status, String message, Map<String, String> errors) {
        return new ApiErrorResponse(status, message, LocalDateTime.now(), errors);
    }
}
