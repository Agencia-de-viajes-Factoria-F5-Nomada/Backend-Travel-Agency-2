package com.inditex.g1_agencia_viajes.exception;

public class PassportRequiredException extends RuntimeException {
    public PassportRequiredException(String message) {
        super(message);
    }
}
