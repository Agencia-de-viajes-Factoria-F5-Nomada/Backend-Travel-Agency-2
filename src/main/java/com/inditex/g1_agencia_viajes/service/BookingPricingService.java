package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingQuotePassengerDetailDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.dto.PassengerRequestDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Booking;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.TypeBoard;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.User;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingPricingService {

    // Reglas de tarifa por edad:
    // 0-2 años  → pagan 5%   (descuento 95%)
    // 2-11 años → pagan 60%  (descuento 40%)
    // 12+ años  → pagan 100% (descuento 0%)
    // Pensionista (65+) → descuento 10% adicional

    private static final int BABY_MAX_AGE      = 2;
    private static final int CHILD_MAX_AGE     = 11;
    private static final int PENSIONER_MIN_AGE = 65;

    private static final BigDecimal BABY_RATE      = BigDecimal.valueOf(0.05);
    private static final BigDecimal CHILD_RATE     = BigDecimal.valueOf(0.60);
    private static final BigDecimal ADULT_RATE     = BigDecimal.valueOf(1.00);
    private static final BigDecimal PENSIONER_RATE = BigDecimal.valueOf(0.90);

    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    public BookingQuoteResponseDTO quote(BookingQuoteRequestDTO request) {
        if (request.getTypeBoard() == null) {
            throw new IllegalArgumentException("El tipo de pensión es obligatorio");
        }
        Travel travel = travelRepository.findById(request.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", request.getTravelId()));

        return buildQuoteFromPassengers(travel, request.getTypeBoard(), request.getPassengers(), request.getIsGroup());
    }

    public BookingQuoteResponseDTO generateQuoteFromBooking(Booking booking) {
        if (booking.getTravel() == null) {
            throw new ResourceNotFoundException("el viaje", null);
        }
        if (booking.getTypeBoard() == null) {
            throw new IllegalArgumentException("El tipo de pensión es obligatorio");
        }
        if (booking.getCustomers() == null || booking.getCustomers().isEmpty()) {
            throw new IllegalArgumentException("Debes indicar al menos un cliente");
        }
        Travel travel = booking.getTravel();
        return buildQuoteFromUsers(travel, booking.getTypeBoard(), booking.getCustomers(), booking.getIsGroup());
    }

    public Double calculateTotalPrice(Booking booking) {
        if (booking.getTravel() == null || booking.getTravel().getId() == null) {
            throw new ResourceNotFoundException("el viaje", null);
        }
        if (booking.getTypeBoard() == null) {
            throw new IllegalArgumentException("El tipo de pensión es obligatorio");
        }
        if (booking.getCustomers() == null || booking.getCustomers().isEmpty()) {
            throw new IllegalArgumentException("Debes indicar al menos un cliente");
        }
        Travel travel = travelRepository.findById(booking.getTravel().getId())
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", booking.getTravel().getId()));
        return buildQuoteFromUsers(travel, booking.getTypeBoard(), booking.getCustomers(), booking.getIsGroup()).getTotalPrice();
    }

    // ── Quote desde pasajeros del frontend (con birthDate) ────────────────
    private BookingQuoteResponseDTO buildQuoteFromPassengers(
            Travel travel, TypeBoard typeBoard,
            List<PassengerRequestDTO> passengers, Boolean isGroup) {

        if (passengers == null || passengers.isEmpty()) {
            throw new IllegalArgumentException("Debes indicar al menos un pasajero");
        }

        Hotel hotel = resolveHotel(travel);
        BigDecimal basePricePerPassenger = resolveBasePrice(hotel, typeBoard);

        List<BookingQuotePassengerDetailDTO> details = new ArrayList<>();
        BigDecimal totalBeforeDiscount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (PassengerRequestDTO p : passengers) {
            int age = calculateAge(p.getBirthDate());
            BigDecimal offerDiscount = resolveOfferDiscount(travel, basePricePerPassenger);
            BigDecimal rate = determineRate(age);
            BigDecimal taxableAmount = basePricePerPassenger.subtract(offerDiscount);
            BigDecimal finalPrice = taxableAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal categoryDiscount = taxableAmount.subtract(finalPrice);

            BookingQuotePassengerDetailDTO detail = new BookingQuotePassengerDetailDTO();
            detail.setName(p.getName());
            detail.setSurname(p.getSurname());
            detail.setAge(age);
            detail.setCategory(determineCategory(age));
            detail.setBasePrice(scale(basePricePerPassenger).doubleValue());
            detail.setOfferDiscountAmount(scale(offerDiscount).doubleValue());
            detail.setCategoryDiscountAmount(scale(categoryDiscount).doubleValue());
            detail.setFinalPrice(scale(finalPrice).doubleValue());

            details.add(detail);
            totalBeforeDiscount = totalBeforeDiscount.add(basePricePerPassenger);
            totalDiscount = totalDiscount.add(offerDiscount).add(categoryDiscount);
        }

        return buildResponse(travel, typeBoard, isGroup, passengers.size(),
                basePricePerPassenger, totalBeforeDiscount, totalDiscount, details);
    }

    // ── Quote desde usuarios registrados (flujo admin / interno) ─────────
    private BookingQuoteResponseDTO buildQuoteFromUsers(
            Travel travel, TypeBoard typeBoard,
            List<User> customers, Boolean isGroup) {

        Hotel hotel = resolveHotel(travel);
        BigDecimal basePricePerPassenger = resolveBasePrice(hotel, typeBoard);

        List<BookingQuotePassengerDetailDTO> details = new ArrayList<>();
        BigDecimal totalBeforeDiscount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (User customer : customers) {
            int age = customer.getAge() != null ? customer.getAge() : 30;
            BigDecimal offerDiscount = resolveOfferDiscount(travel, basePricePerPassenger);
            BigDecimal rate = determineRate(age);
            BigDecimal taxableAmount = basePricePerPassenger.subtract(offerDiscount);
            BigDecimal finalPrice = taxableAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal categoryDiscount = taxableAmount.subtract(finalPrice);

            BookingQuotePassengerDetailDTO detail = new BookingQuotePassengerDetailDTO();
            detail.setName(customer.getName());
            detail.setSurname(customer.getSurname());
            detail.setAge(age);
            detail.setCategory(determineCategory(age));
            detail.setBasePrice(scale(basePricePerPassenger).doubleValue());
            detail.setOfferDiscountAmount(scale(offerDiscount).doubleValue());
            detail.setCategoryDiscountAmount(scale(categoryDiscount).doubleValue());
            detail.setFinalPrice(scale(finalPrice).doubleValue());

            details.add(detail);
            totalBeforeDiscount = totalBeforeDiscount.add(basePricePerPassenger);
            totalDiscount = totalDiscount.add(offerDiscount).add(categoryDiscount);
        }

        return buildResponse(travel, typeBoard, isGroup, customers.size(),
                basePricePerPassenger, totalBeforeDiscount, totalDiscount, details);
    }

    private BookingQuoteResponseDTO buildResponse(
            Travel travel, TypeBoard typeBoard, Boolean isGroup,
            int numPassengers, BigDecimal basePricePerPassenger,
            BigDecimal totalBeforeDiscount, BigDecimal totalDiscount,
            List<BookingQuotePassengerDetailDTO> details) {

        BigDecimal totalPrice = totalBeforeDiscount.subtract(totalDiscount);

        if (Boolean.TRUE.equals(isGroup) && numPassengers >= 10) {
            BigDecimal groupDiscount = totalPrice.multiply(BigDecimal.valueOf(0.05))
                    .setScale(2, RoundingMode.HALF_UP);
            totalDiscount = totalDiscount.add(groupDiscount);
            totalPrice = totalPrice.subtract(groupDiscount);
        }

        BookingQuoteResponseDTO response = new BookingQuoteResponseDTO();
        response.setTravelId(travel.getId());
        response.setTravelDestiny(travel.getDestiny());
        response.setTypeBoard(typeBoard);
        response.setIsGroup(Boolean.TRUE.equals(isGroup));
        response.setPassengers(numPassengers);
        response.setBasePricePerPassenger(scale(basePricePerPassenger).doubleValue());
        response.setTotalBeforeDiscount(scale(totalBeforeDiscount).doubleValue());
        response.setTotalDiscount(scale(totalDiscount).doubleValue());
        response.setTotalPrice(scale(totalPrice).doubleValue());
        response.setPassengerDetails(details);
        return response;
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 30;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private BigDecimal determineRate(int age) {
        if (age < BABY_MAX_AGE)      return BABY_RATE;
        if (age <= CHILD_MAX_AGE)    return CHILD_RATE;
        if (age >= PENSIONER_MIN_AGE) return PENSIONER_RATE;
        return ADULT_RATE;
    }

    private String determineCategory(int age) {
        if (age < BABY_MAX_AGE)       return "BABY";
        if (age <= CHILD_MAX_AGE)     return "CHILD";
        if (age >= PENSIONER_MIN_AGE) return "PENSIONER";
        return "ADULT";
    }

    private Hotel resolveHotel(Travel travel) {
        Hotel hotel = travel.getHotel();
        if (hotel == null) throw new ResourceNotFoundException("el hotel", travel.getId());
        return hotel;
    }

    private BigDecimal resolveBasePrice(Hotel hotel, TypeBoard typeBoard) {
        Double price = typeBoard == TypeBoard.FULL ? hotel.getFullBoardPrice() : hotel.getHalfBoardPrice();
        if (price == null) throw new ResourceNotFoundException("el hotel", hotel.getId());
        return BigDecimal.valueOf(price);
    }

    private BigDecimal resolveOfferDiscount(Travel travel, BigDecimal basePrice) {
        if (!Boolean.TRUE.equals(travel.getSale())
                || travel.getOffer() == null
                || travel.getOffer().getDiscountPercentage() == null) {
            return BigDecimal.ZERO;
        }
        return basePrice.multiply(BigDecimal.valueOf(travel.getOffer().getDiscountPercentage()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}