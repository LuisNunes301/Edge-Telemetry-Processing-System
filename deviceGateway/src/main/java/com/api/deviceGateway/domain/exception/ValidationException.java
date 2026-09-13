package com.api.deviceGateway.domain.exception;

public class ValidationException extends DomainException{
    public ValidationException(String message){
        super(message, "VAL-400");
    }

}
