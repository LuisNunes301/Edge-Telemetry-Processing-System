package com.api.deviceGateway.domain.exception;

public class BusinessRuleException extends DomainException {
    
    public BusinessRuleException(String message) {
        // Código genérico para quebra de regras de negócio (ex: temperatura impossível)
        super(message, "BUS-422");
    }
    
    // Construtor sobrecarregado caso queira passar um código específico
    public BusinessRuleException(String message, String customCode) {
        super(message, customCode);
    }
}