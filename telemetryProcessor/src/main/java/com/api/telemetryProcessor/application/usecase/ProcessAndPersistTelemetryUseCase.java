package com.api.telemetryProcessor.application.usecase;

import com.api.telemetryProcessor.domain.entity.TelemetryEvent;
import com.api.telemetryProcessor.domain.port.out.TelemetryRepositoryPort;

public class ProcessAndPersistTelemetryUseCase {

    private final TelemetryRepositoryPort repositoryPort;

    public ProcessAndPersistTelemetryUseCase(TelemetryRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public void execute(TelemetryEvent event) {
        repositoryPort.save(event);
    }
}