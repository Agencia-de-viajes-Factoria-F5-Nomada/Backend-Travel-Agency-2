package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingQuotePassengerDetailDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.dto.PassengerRequestDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Booking;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.Offer;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.TypeBoard;
import com.inditex.g1_agencia_viajes.model.User;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingPricingServiceTest {

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private UserRepository userRepository;

    private BookingPricingService bookingPricingService;
    private Travel travel;
    private Hotel hotel;
    private User adultUser;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingPricingService = new BookingPricingService(travelRepository, userRepository);

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Hotel Test");
        hotel.setHalfBoardPrice(100.0);
        hotel.setFullBoardPrice(150.0);

        travel = new Travel();
        travel.setId(1L);
        travel.setDestiny("Paris");
        travel.setHotel(hotel);
        travel.setSale(false);

        adultUser = new User();
        adultUser.setId(1L);
        adultUser.setName("Adult");
        adultUser.setSurname("User");
        adultUser.setAge(30);

        booking = new Booking();
        booking.setTravel(travel);
        booking.setTypeBoard(TypeBoard.HALF);
        booking.setCustomers(List.of(adultUser));
        booking.setIsGroup(false);
    }

    @Test
    void quote_ShouldReturnQuoteResponse() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setPassengers(List.of(
                passenger("Adult", "User", 30),
                passenger("Child", "User", 10)
        ));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response).isNotNull();
        assertThat(response.getTravelId()).isEqualTo(1L);
        assertThat(response.getTravelDestiny()).isEqualTo("Paris");
        assertThat(response.getTypeBoard()).isEqualTo(TypeBoard.HALF);
        assertThat(response.getPassengers()).isEqualTo(2);
        assertThat(response.getBasePricePerPassenger()).isEqualTo(100.0);
    }

    @Test
    void quote_WithFullBoard_ShouldUseFullBoardPrice() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.FULL);
        request.setPassengers(List.of(passenger("Adult", "User", 30)));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response.getBasePricePerPassenger()).isEqualTo(150.0);
    }

    @Test
    void quote_ShouldThrowWhenTravelNotFound() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(99L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setPassengers(List.of(passenger("Adult", "User", 30)));
        request.setIsGroup(false);

        when(travelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingPricingService.quote(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void quote_ShouldApplyChildDiscount() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setPassengers(List.of(passenger("Child", "User", 10)));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response.getPassengerDetails()).hasSize(1);
        BookingQuotePassengerDetailDTO detail = response.getPassengerDetails().get(0);
        assertThat(detail.getCategory()).isEqualTo("CHILD");
        assertThat(detail.getCategoryDiscountAmount()).isPositive();
    }

    @Test
    void quote_ShouldApplyPensionerDiscount() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setPassengers(List.of(passenger("Pensioner", "User", 70)));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response.getPassengerDetails()).hasSize(1);
        BookingQuotePassengerDetailDTO detail = response.getPassengerDetails().get(0);
        assertThat(detail.getCategory()).isEqualTo("PENSIONER");
        assertThat(detail.getCategoryDiscountAmount()).isPositive();
    }

    @Test
    void quote_ShouldApplyGroupDiscount() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setPassengers(List.of(
                passenger("User", "One", 30),
                passenger("User", "Two", 30),
                passenger("User", "Three", 30),
                passenger("User", "Four", 30),
                passenger("User", "Five", 30),
                passenger("User", "Six", 30),
                passenger("User", "Seven", 30),
                passenger("User", "Eight", 30),
                passenger("User", "Nine", 30),
                passenger("User", "Ten", 30)
        ));
        request.setIsGroup(true);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response.getIsGroup()).isTrue();
        assertThat(response.getPassengers()).isEqualTo(10);
        assertThat(response.getTotalDiscount()).isPositive();
    }

    @Test
    void calculateTotalPrice_ShouldReturnPrice() {
        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        Double price = bookingPricingService.calculateTotalPrice(booking);

        assertThat(price).isNotNull();
        assertThat(price).isPositive();
    }

    @Test
    void calculateTotalPrice_ShouldThrowWhenTravelIsNull() {
        booking.setTravel(null);

        assertThatThrownBy(() -> bookingPricingService.calculateTotalPrice(booking))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void calculateTotalPrice_ShouldThrowWhenTypeBoardIsNull() {
        booking.setTypeBoard(null);

        assertThatThrownBy(() -> bookingPricingService.calculateTotalPrice(booking))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tipo de pens");
    }

    @Test
    void calculateTotalPrice_ShouldThrowWhenCustomersIsEmpty() {
        booking.setCustomers(List.of());

        assertThatThrownBy(() -> bookingPricingService.calculateTotalPrice(booking))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Debes indicar al menos un cliente");
    }

    @Test
    void quote_WithOfferDiscount_ShouldApplyOffer() {
        Offer offer = new Offer();
        offer.setOfferId(1L);
        offer.setDiscountPercentage(10.0);
        travel.setSale(true);
        travel.setOffer(offer);

        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setPassengers(List.of(passenger("Adult", "User", 30)));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response.getPassengerDetails()).hasSize(1);
        BookingQuotePassengerDetailDTO detail = response.getPassengerDetails().get(0);
        assertThat(detail.getOfferDiscountAmount()).isPositive();
    }

    @Test
    void quote_ShouldThrowWhenNoHotel() {
        travel.setHotel(null);

        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setPassengers(List.of(passenger("Adult", "User", 30)));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        assertThatThrownBy(() -> bookingPricingService.quote(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void quote_ShouldThrowWhenTypeBoardIsNull() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(null);
        request.setPassengers(List.of(passenger("Adult", "User", 30)));
        request.setIsGroup(false);

        assertThatThrownBy(() -> bookingPricingService.quote(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tipo de pens");
    }

    private PassengerRequestDTO passenger(String name, String surname, int yearsAgo) {
        PassengerRequestDTO passenger = new PassengerRequestDTO();
        passenger.setName(name);
        passenger.setSurname(surname);
        passenger.setBirthDate(LocalDate.now().minusYears(yearsAgo));
        return passenger;
    }
}
