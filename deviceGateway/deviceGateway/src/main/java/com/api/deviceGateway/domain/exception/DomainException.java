package com.api.deviceGateway.domain.exception;

public abstract class DomainException extends RuntimeException {
    
    private final String errorCode;

    protected DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}