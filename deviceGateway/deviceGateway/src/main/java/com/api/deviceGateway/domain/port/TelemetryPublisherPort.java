package com.api.deviceGateway.domain.port;

import com.api.deviceGateway.domain.entity.DeviceTelemetry;


public interface TelemetryPublisherPort {
    void publish(DeviceTelemetry telemetry);
}