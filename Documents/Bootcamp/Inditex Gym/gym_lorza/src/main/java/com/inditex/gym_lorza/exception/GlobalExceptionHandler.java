package com.inditex.gym_lorza.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<String> handleObjectNotFound(ObjectNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(PaymentRequiredException.class)
    public ResponseEntity<String> handlePayment(PaymentRequiredException exception) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyEnrolledException.class)
    public ResponseEntity<String> handleAlreadyEnrolled(UserAlreadyEnrolledException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(MaxActivitiesReachedException.class)
    public ResponseEntity<String> handleMaxActivities(MaxActivitiesReachedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(TrainerNotActiveException.class)
    public ResponseEntity<String> handleTrainerNotActive(TrainerNotActiveException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}