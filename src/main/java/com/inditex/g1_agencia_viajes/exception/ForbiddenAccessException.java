package com.inditex.g1_agencia_viajes.exception;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String message) {
        super(message);
    }
}
