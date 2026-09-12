package com.api.deviceGateway.infraestructure.in.rest.dto;

import com.api.deviceGateway.domain.entity.DeviceTelemetry;

public class TelemetryMapper {

    public static DeviceTelemetry toEntity(TelemetryRequestDTO dto) {
        return new DeviceTelemetry(
                dto.deviceId(),
                dto.timestamp(),
                new DeviceTelemetry.Location(dto.location().latitude(), dto.location().longitude()),
                new DeviceTelemetry.Metrics(dto.metrics().temperature(), dto.metrics().humidity(), dto.metrics().batteryLevel()),
                dto.metadata() != null ? new DeviceTelemetry.Metadata(dto.metadata().firmwareVersion(), dto.metadata().networkType()) : null
        );
    }
}