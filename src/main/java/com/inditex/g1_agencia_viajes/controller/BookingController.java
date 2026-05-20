package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BookingRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BookingUserRequestDTO;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas de viajes")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Obtener todas las reservas")
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookings(@PageableDefault(size = 20) Pageable pageable,
                                                                   @RequestAttribute("id") Long currentUserId,
                                                                   @RequestAttribute("role") Role role) {
        return ResponseEntity.ok(bookingService.findAll(pageable, currentUserId, role));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reserva por ID")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id,
                                                             @RequestAttribute("id") Long currentUserId,
                                                             @RequestAttribute("role") Role role) {
        return bookingService.findById(id, currentUserId, role)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO dto,
                                                            @RequestAttribute("id") Long currentUserId,
                                                            @RequestAttribute("role") Role role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.save(dto, currentUserId, role));
    }

    @PostMapping("/quote")
    @Operation(summary = "Calcular precio de una reserva")
    public ResponseEntity<BookingQuoteResponseDTO> quoteBooking(@Valid @RequestBody BookingQuoteRequestDTO request) {
        return ResponseEntity.ok(bookingService.quote(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una reserva existente")
    public ResponseEntity<BookingResponseDTO> updateBooking(@PathVariable Long id,
                                                            @Valid @RequestBody BookingRequestDTO dto,
                                                            @RequestAttribute("id") Long currentUserId,
                                                            @RequestAttribute("role") Role role) {
        return ResponseEntity.ok(bookingService.update(id, dto, currentUserId, role));
    }

    @PostMapping("/{bookingId}/customers")
    @Operation(summary = "Añadir un pasajero a una reserva existente")
    public ResponseEntity<Void> addCustomerToBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingUserRequestDTO request,
            @RequestAttribute("id") Long currentUserId,
            @RequestAttribute("role") Role role) {
        request.setBookingId(bookingId);
        bookingService.addCustomerToBooking(request, currentUserId, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una reserva")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id,
                                              @RequestAttribute("id") Long currentUserId,
                                              @RequestAttribute("role") Role role) {
        bookingService.deleteById(id, currentUserId, role);
        return ResponseEntity.noContent().build();
    }
}
