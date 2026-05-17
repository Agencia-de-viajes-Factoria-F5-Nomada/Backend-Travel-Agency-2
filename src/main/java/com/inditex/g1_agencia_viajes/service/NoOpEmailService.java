package com.inditex.g1_agencia_viajes.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(EmailServiceImpl.class)
public class NoOpEmailService implements EmailService {
    @Override
    public void sendBookingConfirmation(Long bookingId) {
        // Email desactivado en desarrollo
    }
}