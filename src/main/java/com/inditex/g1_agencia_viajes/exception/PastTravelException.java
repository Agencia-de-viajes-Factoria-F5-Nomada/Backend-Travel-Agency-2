package com.inditex.g1_agencia_viajes.exception;

public class PastTravelException extends RuntimeException {
    public PastTravelException(Long id) {
        super("El viaje con el id: " + id + ", ya ha terminado");
    }
}
