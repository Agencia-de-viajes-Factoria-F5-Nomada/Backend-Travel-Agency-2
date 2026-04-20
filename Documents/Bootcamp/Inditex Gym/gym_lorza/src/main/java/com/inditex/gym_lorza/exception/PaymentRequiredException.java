package com.inditex.gym_lorza.exception;

public class PaymentRequiredException extends RuntimeException {
    public PaymentRequiredException(Long userId) {
        super("El usuario con id " + userId + " no ha realizado el pago");
    }
}