package com.inditex.gym_lorza.exception;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(Long id) {
        super("Trainer con id " + id + " no encontrado");
    }
}