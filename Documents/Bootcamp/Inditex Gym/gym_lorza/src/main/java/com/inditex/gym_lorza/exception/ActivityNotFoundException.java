package com.inditex.gym_lorza.exception;

public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException(Long id) {
        super("Actividad con id " + id + " no encontrada");
    }
}