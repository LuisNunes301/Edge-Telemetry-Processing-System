package com.api.telemetryProcessor.infraestructure.out.database;

import org.springframework.stereotype.Component;

import com.api.telemetryProcessor.domain.entity.TelemetryEvent;
import com.api.telemetryProcessor.domain.port.out.TelemetryRepositoryPort;

@Component
public class TelemetryRepositoryAdapter implements TelemetryRepositoryPort {

    private final SpringDataTelemetryRepository springDataRepository;

    public TelemetryRepositoryAdapter(SpringDataTelemetryRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public void save(TelemetryEvent event) {
        TelemetryEntity entity = TelemetryEntity.builder()
                .deviceId(event.deviceId())
                .eventTimestamp(event.timestamp())
                .latitude(event.location().latitude())
                .longitude(event.location().longitude())
                .temperature(event.metrics().temperature())
                .humidity(event.metrics().humidity())
                .batteryLevel(event.metrics().batteryLevel())
                .firmwareVersion(event.metadata().firmwareVersion())
                .networkType(event.metadata().networkType())
                .build();

        springDataRepository.save(entity);
    }
}
