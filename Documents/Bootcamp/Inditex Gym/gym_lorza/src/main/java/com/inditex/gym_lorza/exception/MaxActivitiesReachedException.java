package com.inditex.gym_lorza.exception;

public class MaxActivitiesReachedException extends RuntimeException {
    public MaxActivitiesReachedException(Long userId) {
        super("El usuario " + userId + " ya tiene el máximo de actividades permitidas");
    }
}