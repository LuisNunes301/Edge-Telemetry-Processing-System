package com.api.deviceGateway.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.api.deviceGateway.application.usecase.ProcessTelemetryUseCase;
import com.api.deviceGateway.domain.port.TelemetryPublisherPort;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcessTelemetryUseCase processTelemetryUseCase(TelemetryPublisherPort publisherPort) {
        return new ProcessTelemetryUseCase(publisherPort);
    }
}
