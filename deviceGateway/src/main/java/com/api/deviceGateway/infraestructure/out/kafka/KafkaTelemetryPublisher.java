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

    private final KafkaTemplate<String, String> kafkaTemplate; 
    private final String defaultTopic;
    private final ObjectMapper objectMapper;                   
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
        log.info("====> GATEWAY [1]: Iniciando publicacao. Topico Alvo: '{}', Device: {}", defaultTopic, telemetry.deviceId());
        try {
            String payloadJson = objectMapper.writeValueAsString(telemetry);
            log.info("====> GATEWAY [2]: JSON convertido com sucesso: {}", payloadJson);

            kafkaTemplate.send(defaultTopic, telemetry.deviceId(), payloadJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("====> GATEWAY [3]: SUCESSO ABSOLUTO! Mensagem no Kafka. Offset: {}", result.getRecordMetadata().offset());
                        } else {
                            log.error("====> GATEWAY [ERRO KAFKA]: Falha ao publicar: {}", ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("====> GATEWAY [ERRO SERIALIZAÇÃO]: O Jackson falhou ao converter o objeto: ", e);
            throw new RuntimeException("Falha na serialização", e);
        }
    }
}