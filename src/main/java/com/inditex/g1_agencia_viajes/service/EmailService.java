package com.inditex.g1_agencia_viajes.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendBookingConfirmation(Long bookingId) throws MessagingException;
}