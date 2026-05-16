package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingQuotePassengerDetailDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingPricingService {

    private static final BigDecimal CHILD_DISCOUNT = BigDecimal.valueOf(15);
    private static final BigDecimal PENSIONER_DISCOUNT = BigDecimal.valueOf(10);
    private static final int CHILD_MAX_AGE = 17;
    private static final int PENSIONER_MIN_AGE = 65;

    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public BookingQuoteResponseDTO quote(BookingQuoteRequestDTO request) {
        if (request.getTypeBoard() == null) {
            throw new IllegalArgumentException("El tipo de pensión es obligatorio");
        }
        Travel travel = travelRepository.findById(request.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", request.getTravelId()));
        List<User> customers = loadUsers(request.getCustomerIds());
        return buildQuote(travel, request.getTypeBoard(), customers, request.getIsGroup());
    }

    @Transactional(readOnly = true)
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
        return buildQuote(travel, booking.getTypeBoard(), booking.getCustomers(), booking.getIsGroup());
    }

    @Transactional(readOnly = true)
    public Double calculateTotalPrice(Booking booking) {
        if (booking.getTravel() == null) {
            throw new ResourceNotFoundException("el viaje", null);
        }
        if (booking.getTravel().getId() == null) {
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
        BookingQuoteResponseDTO quote = buildQuote(travel, booking.getTypeBoard(), booking.getCustomers(), booking.getIsGroup());
        return quote.getTotalPrice();
    }

    private List<User> loadUsers(List<Long> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> users = userRepository.findAllById(customerIds);
        for (Long id : customerIds) {
            if (users.stream().noneMatch(u -> u.getId().equals(id))) {
                throw new ResourceNotFoundException("el cliente", id);
            }
        }
        return users;
    }

    private BookingQuoteResponseDTO buildQuote(Travel travel, TypeBoard typeBoard, List<User> customers, Boolean isGroup) {
        if (typeBoard == null) {
            throw new IllegalArgumentException("El tipo de pensión es obligatorio");
        }
        if (customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException("Debes indicar al menos un cliente");
        }
        Hotel hotel = travel.getHotel();
        if (hotel == null) {
            throw new ResourceNotFoundException("el hotel", travel.getId());
        }

        BigDecimal basePricePerPassenger = resolveBasePrice(hotel, typeBoard);
        List<BookingQuotePassengerDetailDTO> passengerDetails = new ArrayList<>();
        BigDecimal totalBeforeDiscount = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (User customer : customers) {
            PassengerPrice passengerPrice = calculatePassengerPrice(customer, basePricePerPassenger, travel);
            passengerDetails.add(passengerPrice.toDTO());
            totalBeforeDiscount = totalBeforeDiscount.add(passengerPrice.basePrice);
            totalDiscount = totalDiscount.add(passengerPrice.offerDiscountAmount)
                    .add(passengerPrice.categoryDiscountAmount);
        }

        BigDecimal totalPrice = totalBeforeDiscount.subtract(totalDiscount);
        if (Boolean.TRUE.equals(isGroup) && customers.size() >= 10) {
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
        response.setPassengers(customers.size());
        response.setBasePricePerPassenger(scale(basePricePerPassenger).doubleValue());
        response.setTotalBeforeDiscount(scale(totalBeforeDiscount).doubleValue());
        response.setTotalDiscount(scale(totalDiscount).doubleValue());
        response.setTotalPrice(scale(totalPrice).doubleValue());
        response.setPassengerDetails(passengerDetails);
        return response;
    }

    private BigDecimal resolveBasePrice(Hotel hotel, TypeBoard typeBoard) {
        Double price = typeBoard == TypeBoard.FULL ? hotel.getFullBoardPrice() : hotel.getHalfBoardPrice();
        if (price == null) {
            throw new ResourceNotFoundException("el hotel", hotel.getId());
        }
        return BigDecimal.valueOf(price);
    }

    private PassengerPrice calculatePassengerPrice(User customer, BigDecimal basePrice, Travel travel) {
        PassengerPrice passengerPrice = new PassengerPrice();
        passengerPrice.userId = customer.getId();
        passengerPrice.fullName = customer.getName() + " " + customer.getSurname();
        passengerPrice.age = customer.getAge();
        passengerPrice.basePrice = basePrice;

        BigDecimal discountPercentage = determinePassengerDiscount(customer);
        BigDecimal offerDiscount = resolveOfferDiscount(travel, basePrice);
        BigDecimal taxableAmount = basePrice.subtract(offerDiscount);
        BigDecimal categoryDiscount = taxableAmount.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        passengerPrice.offerDiscountAmount = offerDiscount;
        passengerPrice.categoryDiscountAmount = categoryDiscount;
        passengerPrice.discountAmount = offerDiscount.add(categoryDiscount);
        passengerPrice.finalPrice = taxableAmount.subtract(categoryDiscount);
        passengerPrice.category = determineCategory(customer);
        return passengerPrice;
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

    private BigDecimal determinePassengerDiscount(User customer) {
        Integer age = customer.getAge();
        if (age == null) {
            return BigDecimal.ZERO;
        }
        if (age <= CHILD_MAX_AGE) {
            return CHILD_DISCOUNT;
        }
        if (age >= PENSIONER_MIN_AGE) {
            return PENSIONER_DISCOUNT;
        }
        return BigDecimal.ZERO;
    }

    private String determineCategory(User customer) {
        Integer age = customer.getAge();
        if (age == null) {
            return "UNKNOWN";
        }
        if (age <= CHILD_MAX_AGE) {
            return "CHILD";
        }
        if (age >= PENSIONER_MIN_AGE) {
            return "PENSIONER";
        }
        return "ADULT";
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private final class PassengerPrice {
        private Long userId;
        private String fullName;
        private Integer age;
        private String category;
        private BigDecimal basePrice;
        private BigDecimal offerDiscountAmount;
        private BigDecimal categoryDiscountAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalPrice;

        private BookingQuotePassengerDetailDTO toDTO() {
            BookingQuotePassengerDetailDTO dto = new BookingQuotePassengerDetailDTO();
            dto.setUserId(userId);
            dto.setFullName(fullName);
            dto.setAge(age);
            dto.setCategory(category);
            dto.setBasePrice(scale(basePrice).doubleValue());
            dto.setOfferDiscountAmount(scale(offerDiscountAmount).doubleValue());
            dto.setCategoryDiscountAmount(scale(categoryDiscountAmount).doubleValue());
            dto.setFinalPrice(scale(finalPrice).doubleValue());
            return dto;
        }
    }
}
