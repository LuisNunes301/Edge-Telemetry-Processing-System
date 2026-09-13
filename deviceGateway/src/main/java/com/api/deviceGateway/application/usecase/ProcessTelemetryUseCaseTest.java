// package com.api.deviceGateway.application.usecase;


// import com.api.deviceGateway.domain.entity.DeviceTelemetry;
// import com.api.deviceGateway.domain.port.TelemetryPublisherPort;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.time.Instant;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// import com.api.deviceGateway.domain.exception.ValidationException;

// @ExtendWith(MockitoExtension.class)
// class ProcessTelemetryUseCaseTest {

//     @Mock
//     private TelemetryPublisherPort publisherPort;

//     @InjectMocks
//     private ProcessTelemetryUseCase useCase;

//     private DeviceTelemetry validTelemetry;

//     @BeforeEach
//     void setUp() {
//         // Massa de dados baseada no esperado do Edge
//         validTelemetry = new DeviceTelemetry(
//                 "TRK-CAM-092",
//                 Instant.parse("2026-09-12T00:17:25Z"),
//                 new DeviceTelemetry.Location(-12.697341, -38.323712),
//                 new DeviceTelemetry.Metrics(-1.24, 48.7, 99.6),
//                 new DeviceTelemetry.Metadata("1.2.4", "4G")
//         );
//     }

//     @Test
//     @DisplayName("Deve publicar telemetria no broker com sucesso quando entidade for válida")
//     void shouldPublishTelemetrySuccessfullyWhenEntityIsValid() {
//         // Act
//         useCase.execute(validTelemetry);

//         // Assert
//         // Verifica se a porta de saída foi chamada exatamente 1 vez com o objeto correto
//         verify(publisherPort, times(1)).publish(validTelemetry);
//     }

//     @Test
//     @DisplayName("Deve lançar ValidationException quando deviceId for nulo")
//     void shouldThrowExceptionWhenDeviceIdIsNull() {
//         // Arrange - Criando um clone inválido
//         DeviceTelemetry invalidTelemetry = new DeviceTelemetry(
//                 null, 
//                 validTelemetry.timestamp(), 
//                 validTelemetry.location(), 
//                 validTelemetry.metrics(), 
//                 validTelemetry.metadata()
//         );

//         // Act & Assert
//         ValidationException exception = assertThrows(ValidationException.class, () -> {
//             useCase.execute(invalidTelemetry);
//         });

//         // Valida se a mensagem e o código customizado estão corretos
//         assertEquals("O Identificador do Dispositivo (deviceId) é obrigatório.", exception.getMessage());
//         assertEquals("VAL-400", exception.getErrorCode());
        
//         // Garante que a porta de saída NUNCA foi chamada
//         verify(publisherPort, never()).publish(any());
//     }

//     @Test
//     @DisplayName("Deve lançar ValidationException quando deviceId for vazio ou espaços")
//     void shouldThrowExceptionWhenDeviceIdIsBlank() {
//         // Arrange
//         DeviceTelemetry invalidTelemetry = new DeviceTelemetry(
//                 "   ", 
//                 validTelemetry.timestamp(), 
//                 validTelemetry.location(), 
//                 validTelemetry.metrics(), 
//                 validTelemetry.metadata()
//         );

//         // Act & Assert
//         assertThrows(ValidationException.class, () -> useCase.execute(invalidTelemetry));
//         verify(publisherPort, never()).publish(any());
//     }
// }