package com.api.telemetryProcessor.infraestructure.out.database;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "telemetry_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant eventTimestamp;
    
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double humidity;
    private Double batteryLevel;
    private String firmwareVersion;
    private String networkType;
}