package com.api.deviceGateway.infraestructure.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.deviceGateway.application.usecase.ProcessTelemetryUseCase;
import com.api.deviceGateway.domain.entity.DeviceTelemetry;
import com.api.deviceGateway.infraestructure.in.rest.dto.TelemetryMapper;
import com.api.deviceGateway.infraestructure.in.rest.dto.TelemetryRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/telemetry")
@Tag(name = "Telemetry Ingestion", description = "API de borda para recebimento de telemetria de frotas IoT")
public class TelemetryController {

    private final ProcessTelemetryUseCase useCase;

    public TelemetryController(ProcessTelemetryUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @Operation(summary = "Recebe e valida dados de telemetria", description = "Valida o payload estruturado do dispositivo IoT e o encaminha para o broker de mensageria de forma assíncrona.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Telemetria aceita e enfileirada com sucesso para processamento."),
        @ApiResponse(responseCode = "400", description = "Erro de validação nos campos do payload (ex: campos obrigatórios ausentes).", content = @Content),
        @ApiResponse(responseCode = "422", description = "Erro de regra de negócio no domínio.", content = @Content)
    })
    public ResponseEntity<Void> receiveTelemetry(@Valid @RequestBody TelemetryRequestDTO requestDTO) {
        // 1. Converte DTO de Infraestrutura para Entidade de Domínio
        DeviceTelemetry telemetry = TelemetryMapper.toEntity(requestDTO);
        
        useCase.execute(telemetry);
        
        return ResponseEntity.accepted().build();
    }
}
