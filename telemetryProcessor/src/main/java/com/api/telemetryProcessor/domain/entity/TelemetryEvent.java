package com.api.telemetryProcessor.domain.entity;

import java.time.Instant;

public record TelemetryEvent(
    String deviceId,
    Instant timestamp,
    Location location,
    Metrics metrics,
    Metadata metadata
) {
    public record Location(Double latitude, Double longitude) {}
    public record Metrics(Double temperature, Double humidity, Double batteryLevel) {}
    public record Metadata(String firmwareVersion, String networkType) {}
}