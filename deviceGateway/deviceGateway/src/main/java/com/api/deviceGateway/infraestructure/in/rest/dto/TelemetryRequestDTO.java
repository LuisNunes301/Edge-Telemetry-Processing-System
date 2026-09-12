package com.api.deviceGateway.infraestructure.in.rest.dto;

import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TelemetryRequestDTO(
        @NotBlank(message = "deviceId é obrigatório")
        String deviceId,
        
        @NotNull(message = "timestamp é obrigatório")
        Instant timestamp,
        
        @NotNull(message = "location é obrigatório")
        @Valid LocationDTO location,
        
        @NotNull(message = "metrics é obrigatório")
        @Valid MetricsDTO metrics,
        
        MetadataDTO metadata
) {
    public record LocationDTO(
            @NotNull Double latitude, 
            @NotNull Double longitude
    ) {}

    public record MetricsDTO(
            @NotNull Double temperature, 
            @NotNull Double humidity, 
            @NotNull Double batteryLevel
    ) {}

    public record MetadataDTO(String firmwareVersion, String networkType) {}
}