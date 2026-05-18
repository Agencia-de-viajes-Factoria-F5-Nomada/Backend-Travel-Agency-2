package com.inditex.g1_agencia_viajes.service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class NoOpEmailService implements EmailService {
    @Override
    public void sendBookingConfirmation(Long bookingId) throws MessagingException {
    }
}