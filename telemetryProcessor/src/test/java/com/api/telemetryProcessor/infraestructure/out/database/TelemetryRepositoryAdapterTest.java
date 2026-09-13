package com.api.telemetryProcessor.infraestructure.out.database;

import com.api.telemetryProcessor.domain.entity.TelemetryEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9999", 
    "spring.kafka.listener.auto-startup=false" 
    ,"spring.kafka.admin.enabled=false"     
})

class TelemetryRepositoryAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create"); // Mudado de create-drop para create
    }

    @Autowired
    private TelemetryRepositoryAdapter repositoryAdapter;

    @Autowired
    private SpringDataTelemetryRepository springDataRepository;

    @Test
    @DisplayName("Deve salvar entidade no PostgreSQL usando Testcontainers")
    void shouldSaveEntitySuccessfully() {
        // Arrange
        TelemetryEvent event = new TelemetryEvent(
            "DEV-CONTAINER-01",
            Instant.now(),
            new TelemetryEvent.Location(-12.0, -38.0),
            new TelemetryEvent.Metrics(28.0, 50.0, 88.0),
            new TelemetryEvent.Metadata("2.0.1", "5G")
        );

        // Act
        repositoryAdapter.save(event);

        // Assert
        var entities = springDataRepository.findAll();
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getDeviceId()).isEqualTo("DEV-CONTAINER-01");
        assertThat(entities.get(0).getTemperature()).isEqualTo(28.0);
    }
}