package com.api.telemetryProcessor.application.config;

import com.api.telemetryProcessor.application.usecase.ProcessAndPersistTelemetryUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.api.telemetryProcessor.domain.port.out.TelemetryRepositoryPort;


@Configuration
public class BeanConfiguration {

    @Bean
    public ProcessAndPersistTelemetryUseCase processAndPersistTelemetryUseCase(TelemetryRepositoryPort repositoryPort,
    MeterRegistry meterRegistry) {
        return new ProcessAndPersistTelemetryUseCase(repositoryPort,meterRegistry);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public NewTopic telemetryTopic() {
        return TopicBuilder.name("telemetry.ingestion")
                .partitions(1)
                .replicas(1)
                .build();
    }
}