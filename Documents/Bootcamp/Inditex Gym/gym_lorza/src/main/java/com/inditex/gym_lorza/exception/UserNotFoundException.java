package com.inditex.gym_lorza.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Usuario con id " + id + " no encontrado");
    }
}