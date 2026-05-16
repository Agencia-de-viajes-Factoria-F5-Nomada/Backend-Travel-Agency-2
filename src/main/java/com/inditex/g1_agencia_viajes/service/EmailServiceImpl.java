package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Booking;
import com.inditex.g1_agencia_viajes.model.TypeBoard;
import com.inditex.g1_agencia_viajes.repository.BookingRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final BookingRepository bookingRepository;
    private final BookingPricingService bookingPricingService;

    @Value("${spring.mail.from}")
    private String fromEmail;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendBookingConfirmation(Long bookingId) {
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

        try {
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
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email de confirmación", e);
        }
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
