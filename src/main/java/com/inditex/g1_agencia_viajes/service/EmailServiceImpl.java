package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Booking;
import com.inditex.g1_agencia_viajes.model.TypeBoard;
import com.inditex.g1_agencia_viajes.repository.BookingRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@ConditionalOnBean(JavaMailSender.class)
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final BookingRepository bookingRepository;
    private final BookingPricingService bookingPricingService;

    @Value("${spring.mail.from:no-reply@travelagency.com}")
    private String fromEmail;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Async
    @Retryable(
        retryFor = MessagingException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendBookingConfirmation(Long bookingId) throws MessagingException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("la reserva", bookingId));

        BookingQuoteResponseDTO quote = bookingPricingService.generateQuoteFromBooking(booking);

        Context context = new Context();
        context.setVariable("bookingId", booking.getBookingId());
        context.setVariable("destiny", booking.getTravel().getDestiny());
        context.setVariable("startDate", booking.getTravel().getStartDate().format(DATE_FORMATTER));
        context.setVariable("endDate", booking.getTravel().getEndDate().format(DATE_FORMATTER));
        context.setVariable("hotelName", booking.getTravel().getHotel().getName());
        context.setVariable("typeBoard", formatTypeBoard(booking.getTypeBoard()));
        context.setVariable("isGroup", Boolean.TRUE.equals(booking.getIsGroup()));
        context.setVariable("passengers", quote.getPassengerDetails());
        context.setVariable("basePricePerPassenger", quote.getBasePricePerPassenger());
        context.setVariable("totalBeforeDiscount", quote.getTotalBeforeDiscount());
        context.setVariable("totalDiscount", quote.getTotalDiscount());
        context.setVariable("totalPrice", quote.getTotalPrice());

        String html = templateEngine.process("email/booking-confirmation", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(resolveToEmails(booking));
        helper.setSubject("Confirmación de Reserva #" + booking.getBookingId() + " - " + booking.getTravel().getDestiny());
        helper.setText(html, true);

        if (booking.getEmployee() != null && booking.getEmployee().getEmail() != null) {
            helper.setCc(booking.getEmployee().getEmail());
        }

        mailSender.send(message);
        log.info("Email de confirmación enviado exitosamente para reserva {}", bookingId);
    }

    @Recover
    public void recoverFromEmailFailure(MessagingException e, Long bookingId) {
        log.error("Fallo definitivo al enviar email tras 3 reintentos para reserva {}: {}",
                 bookingId, e.getMessage());
    }

    private String[] resolveToEmails(Booking booking) {
        List<String> emails = booking.getCustomers().stream()
                .map(u -> u.getEmail())
                .filter(Objects::nonNull)
                .filter(e -> !e.isBlank())
                .toList();
        return emails.toArray(new String[0]);
    }

    private String formatTypeBoard(TypeBoard typeBoard) {
        return typeBoard == TypeBoard.FULL ? "Pensión completa" : "Media pensión";
    }
}