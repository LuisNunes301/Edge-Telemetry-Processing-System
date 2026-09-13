package com.api.telemetryProcessor.infraestructure.in.kafka;

import com.api.telemetryProcessor.application.usecase.ProcessAndPersistTelemetryUseCase;
import com.api.telemetryProcessor.domain.entity.TelemetryEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetryKafkaConsumerTest {

    @Mock
    private ProcessAndPersistTelemetryUseCase useCase;

    // Usamos um Spy com o ObjectMapper real para testar o mapeamento exato do Jackson
    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private TelemetryKafkaConsumer kafkaConsumer;

    @BeforeEach
    void setUp() {
        // Garante suporte ao tipo Instant do Java 8+ nos testes
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Deve desserializar a string JSON do Kafka com sucesso e invocar o Caso de Uso")
    void shouldConsumeAndDeserializeMessageSuccessfully() {
        // Arrange
        String jsonMessage = "{" +
                "\"deviceId\":\"TRK-CAM-092\"," +
                "\"timestamp\":\"2026-09-13T03:48:17.437017Z\"," +
                "\"location\":{\"latitude\":-12.698646,\"longitude\":-38.324423}," +
                "\"metrics\":{\"temperature\":-2.26,\"humidity\":54.7,\"batteryLevel\":96.5}," +
                "\"metadata\":{\"firmwareVersion\":\"1.2.4\",\"networkType\":\"4G\"}" +
                "}";

        // Act
        kafkaConsumer.consume(jsonMessage);

        // Assert
        ArgumentCaptor<TelemetryEvent> eventCaptor = ArgumentCaptor.forClass(TelemetryEvent.class);
        verify(useCase, times(1)).execute(eventCaptor.capture());

        TelemetryEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.deviceId()).isEqualTo("TRK-CAM-092");
        assertThat(capturedEvent.metrics().temperature()).isEqualTo(-2.26);
        assertThat(capturedEvent.location().latitude()).isEqualTo(-12.698646);
        assertThat(capturedEvent.metadata().firmwareVersion()).isEqualTo("1.2.4");
    }

    @Test
    @DisplayName("Deve capturar erro e ignorar graciosamente quando receber um JSON corrompido")
    void shouldHandleMalformedJsonGracefullyWithoutThrowingException() {
        // Arrange
        String malformedJson = "{ invalid-json-string }";

        // Act & Assert
        // O método consume deve lidar com a exceção no try-catch interno sem propagar para o Kafka
        kafkaConsumer.consume(malformedJson);

        verify(useCase, never()).execute(any());
    }
}