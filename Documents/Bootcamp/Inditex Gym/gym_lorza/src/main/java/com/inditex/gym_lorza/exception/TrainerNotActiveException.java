package com.inditex.gym_lorza.exception;

public class TrainerNotActiveException extends RuntimeException {

    public TrainerNotActiveException(Long trainerId) {
        super("La entrenadora con id " + trainerId + " no está dada de alta en la empresa");
    }
}