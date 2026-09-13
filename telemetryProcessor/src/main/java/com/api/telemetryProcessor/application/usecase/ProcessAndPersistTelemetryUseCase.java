package com.api.telemetryProcessor.application.usecase;

import org.springframework.stereotype.Component;

import com.api.telemetryProcessor.domain.entity.TelemetryEvent;
import com.api.telemetryProcessor.domain.port.out.TelemetryRepositoryPort;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class ProcessAndPersistTelemetryUseCase {

    private final TelemetryRepositoryPort repositoryPort;
    private final Counter successCounter;
    private final Counter errorCounter;

    public ProcessAndPersistTelemetryUseCase(TelemetryRepositoryPort repositoryPort, MeterRegistry registry) {
        this.repositoryPort = repositoryPort;
        this.successCounter = registry.counter("telemetry.processed.success");
        this.errorCounter = registry.counter("telemetry.processed.errors");
    }

    public void execute(TelemetryEvent event) {
        try {
            repositoryPort.save(event);
            successCounter.increment(); // Incrementa a métrica de sucesso para o Prometheus
        } catch (Exception e) {
            errorCounter.increment(); // Incrementa a métrica de erro
            throw e;
        }
    }
}