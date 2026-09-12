package com.api.deviceGateway.infraestructure.out.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.api.deviceGateway.domain.entity.DeviceTelemetry;
import com.api.deviceGateway.domain.port.TelemetryPublisherPort;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;


@Slf4j
@Component
public class KafkaTelemetryPublisher implements TelemetryPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate; // Alterado para <String, String>
    private final String defaultTopic;
    private final ObjectMapper objectMapper;                    // Serializador manual limpo
    private final MeterRegistry meterRegistry;

    public KafkaTelemetryPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${spring.kafka.template.default-topic}") String defaultTopic,
            ObjectMapper objectMapper,
            MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.defaultTopic = defaultTopic;
        this.objectMapper = objectMapper;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void publish(DeviceTelemetry telemetry) {
        try {
            // Converte a entidade de domínio diretamente para uma String JSON limpa
            String payloadJson = objectMapper.writeValueAsString(telemetry);

            kafkaTemplate.send(defaultTopic, telemetry.deviceId(), payloadJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            meterRegistry.counter("kafka.publish.success", "topic", defaultTopic).increment();
                            log.debug("Telemetria enviada com sucesso: {}", telemetry.deviceId());
                        } else {
                            meterRegistry.counter("kafka.publish.error", "topic", defaultTopic).increment();
                            log.error("Falha ao publicar no Kafka. Device: {}. Erro: {}", 
                                    telemetry.deviceId(), ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Erro crítico ao serializar telemetria para JSON. Device: {}", telemetry.deviceId(), e);
            throw new RuntimeException("Falha na serialização da mensagem", e);
        }
    }
}