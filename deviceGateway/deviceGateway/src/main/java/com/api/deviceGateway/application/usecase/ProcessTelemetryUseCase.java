package com.api.deviceGateway.application.usecase;

import com.api.deviceGateway.domain.entity.DeviceTelemetry;
import com.api.deviceGateway.domain.port.TelemetryPublisherPort;

import jakarta.validation.ValidationException;

public class ProcessTelemetryUseCase {


    private final TelemetryPublisherPort publisher;

    public ProcessTelemetryUseCase(TelemetryPublisherPort publisher){
        this.publisher = publisher;
    }

    public void execute(DeviceTelemetry telemetry){

        if( telemetry.deviceId() == null || telemetry.deviceId().isBlank()){
            throw new ValidationException("Device Id must be real");
        }
        publisher.publish(telemetry);
    }
}
