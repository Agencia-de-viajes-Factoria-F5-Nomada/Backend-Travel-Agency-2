package com.inditex.gym_lorza.exception;

public class UserAlreadyEnrolledException extends RuntimeException {
    public UserAlreadyEnrolledException(Long userId, Long activityId) {
        super("El usuario " + userId + " ya está apuntado a la actividad " + activityId);
    }
}