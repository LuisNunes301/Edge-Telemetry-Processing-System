package com.api.telemetryProcessor.infraestructure.out.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SpringDataTelemetryRepository extends JpaRepository<TelemetryEntity, Long> {
}