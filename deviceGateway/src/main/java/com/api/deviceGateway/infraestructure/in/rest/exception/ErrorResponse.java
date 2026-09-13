package com.api.deviceGateway.infraestructure.in.rest.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String errorCode,
        String message,
        List<FieldError> details
) {
    public record FieldError(String field, String error) {}
}