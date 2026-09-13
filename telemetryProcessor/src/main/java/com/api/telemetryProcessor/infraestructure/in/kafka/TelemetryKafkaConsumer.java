package com.api.telemetryProcessor.infraestructure.in.kafka;

import com.api.telemetryProcessor.domain.entity.TelemetryEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.api.telemetryProcessor.application.usecase.ProcessAndPersistTelemetryUseCase;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TelemetryKafkaConsumer {

    private final ProcessAndPersistTelemetryUseCase useCase;
    private final ObjectMapper objectMapper;

    public TelemetryKafkaConsumer(ProcessAndPersistTelemetryUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
        log.info("====> TELEMETRY KAFKA CONSUMER FOI INICIALIZADO PELO SPRING! <====");
    }

    @KafkaListener(topics = "telemetry.ingestion", groupId = "telemetry-processor-group-v2")
    public void consume(String message) {
        log.info("====> PROCESSOR [RECEBIDO DO KAFKA]: {}", message);
        try {
            TelemetryEvent event = objectMapper.readValue(message, TelemetryEvent.class);
            useCase.execute(event);
            log.info("====> PROCESSOR [SUCESSO]: Evento salvo no PostgreSQL para o device: {}", event.deviceId());
        } catch (Exception e) {
            log.error("====> PROCESSOR [ERRO]: Falha ao processar mensagem do Kafka: {}", message, e);
        }
    }
}