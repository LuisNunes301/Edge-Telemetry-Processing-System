package com.api.telemetryProcessor.application.usecase;

import com.api.telemetryProcessor.domain.entity.TelemetryEvent;
import com.api.telemetryProcessor.domain.port.out.TelemetryRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

class ProcessAndPersistTelemetryUseCaseTest {

    @Test
    @DisplayName("Deve processar e delegar a persistência do evento de telemetria com sucesso")
    void shouldProcessAndPersistSuccessfully() {
        // Arrange
        TelemetryRepositoryPort repositoryPort = Mockito.mock(TelemetryRepositoryPort.class);
        ProcessAndPersistTelemetryUseCase useCase = new ProcessAndPersistTelemetryUseCase(repositoryPort);

        TelemetryEvent event = new TelemetryEvent(
            "TRK-TEST-01",
            Instant.now(),
            new TelemetryEvent.Location(-12.97, -38.50),
            new TelemetryEvent.Metrics(22.5, 65.0, 95.0),
            new TelemetryEvent.Metadata("1.0.0", "4G")
        );

        // Act
        useCase.execute(event);

        // Assert
        Mockito.verify(repositoryPort, Mockito.times(1)).save(event);
    }
}