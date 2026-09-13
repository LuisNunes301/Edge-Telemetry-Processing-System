package com.api.telemetryProcessor.domain.port.out;


import com.api.telemetryProcessor.domain.entity.TelemetryEvent;


public interface TelemetryRepositoryPort {
    void save(TelemetryEvent event);
}
