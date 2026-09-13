package com.api.telemetryProcessor.application.usecase;

import com.api.telemetryProcessor.domain.entity.TelemetryEvent;
import com.api.telemetryProcessor.domain.port.out.TelemetryRepositoryPort;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessAndPersistTelemetryUseCaseTest {

    @Mock
    private TelemetryRepositoryPort repositoryPort;

    @Mock
    private MeterRegistry meterRegistry; // <-- Adicione o mock do MeterRegistry

    @Mock
    private Counter counter; // <-- Mock opcional do Counter para evitar NullPointerException se o UseCase registrar métrica no construtor

    @InjectMocks
    private ProcessAndPersistTelemetryUseCase useCase;

    @Test
    @DisplayName("Deve processar e persistir o evento de telemetria com sucesso")
    void shouldProcessAndPersistSuccessfully() {
        // Configura o mock do registry para retornar um counter válido caso seja chamado
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Arrange
        TelemetryEvent event = new TelemetryEvent(
            "TRK-CAM-092",
            Instant.now(),
            new TelemetryEvent.Location(-12.697341, -38.323712),
            new TelemetryEvent.Metrics(-1.24, 48.7, 99.6),
            new TelemetryEvent.Metadata("1.2.4", "4G")
        );

        // Act
        useCase.execute(event);

        // Assert
        verify(repositoryPort, times(1)).save(event);
    }
}