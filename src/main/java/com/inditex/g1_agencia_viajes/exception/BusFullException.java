package com.inditex.g1_agencia_viajes.exception;

public class BusFullException extends RuntimeException {
    public BusFullException(Long busId, String licensePlate) {
        super("El autobús con matrícula " + licensePlate + " (id: " + busId + ") no tiene suficientes plazas disponibles");
    }
}
