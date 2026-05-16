package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingQuotePassengerDetailDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.*;
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
    private User childUser;
    private User pensionerUser;
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
        travel.setDestiny("París");
        travel.setHotel(hotel);
        travel.setSale(false);

        adultUser = new User();
        adultUser.setId(1L);
        adultUser.setName("Adult");
        adultUser.setSurname("User");
        adultUser.setAge(30);

        childUser = new User();
        childUser.setId(2L);
        childUser.setName("Child");
        childUser.setSurname("User");
        childUser.setAge(10);

        pensionerUser = new User();
        pensionerUser.setId(3L);
        pensionerUser.setName("Pensioner");
        pensionerUser.setSurname("User");
        pensionerUser.setAge(70);

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
        request.setCustomerIds(List.of(1L, 2L));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findAllById(any())).thenReturn(List.of(adultUser, childUser));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response).isNotNull();
        assertThat(response.getTravelId()).isEqualTo(1L);
        assertThat(response.getTravelDestiny()).isEqualTo("París");
        assertThat(response.getTypeBoard()).isEqualTo(TypeBoard.HALF);
        assertThat(response.getPassengers()).isEqualTo(2);
        assertThat(response.getBasePricePerPassenger()).isEqualTo(100.0);
    }

    @Test
    void quote_WithFullBoard_ShouldUseFullBoardPrice() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.FULL);
        request.setCustomerIds(List.of(1L));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findAllById(any())).thenReturn(List.of(adultUser));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response.getBasePricePerPassenger()).isEqualTo(150.0);
    }

    @Test
    void quote_ShouldThrowWhenTravelNotFound() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(99L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setCustomerIds(List.of(1L));
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
        request.setCustomerIds(List.of(2L));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findAllById(any())).thenReturn(List.of(childUser));

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
        request.setCustomerIds(List.of(3L));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findAllById(any())).thenReturn(List.of(pensionerUser));

        BookingQuoteResponseDTO response = bookingPricingService.quote(request);

        assertThat(response.getPassengerDetails()).hasSize(1);
        BookingQuotePassengerDetailDTO detail = response.getPassengerDetails().get(0);
        assertThat(detail.getCategory()).isEqualTo("PENSIONER");
        assertThat(detail.getCategoryDiscountAmount()).isPositive();
    }

    @Test
    void quote_ShouldApplyGroupDiscount() {
        User user1 = new User(); user1.setId(4L); user1.setName("User"); user1.setSurname("One"); user1.setAge(30);
        User user2 = new User(); user2.setId(5L); user2.setName("User"); user2.setSurname("Two"); user2.setAge(30);
        User user3 = new User(); user3.setId(6L); user3.setName("User"); user3.setSurname("Three"); user3.setAge(30);
        User user4 = new User(); user4.setId(7L); user4.setName("User"); user4.setSurname("Four"); user4.setAge(30);
        User user5 = new User(); user5.setId(8L); user5.setName("User"); user5.setSurname("Five"); user5.setAge(30);
        User user6 = new User(); user6.setId(9L); user6.setName("User"); user6.setSurname("Six"); user6.setAge(30);
        User user7 = new User(); user7.setId(10L); user7.setName("User"); user7.setSurname("Seven"); user7.setAge(30);
        User user8 = new User(); user8.setId(11L); user8.setName("User"); user8.setSurname("Eight"); user8.setAge(30);
        User user9 = new User(); user9.setId(12L); user9.setName("User"); user9.setSurname("Nine"); user9.setAge(30);
        User user10 = new User(); user10.setId(13L); user10.setName("User"); user10.setSurname("Ten"); user10.setAge(30);

        List<Long> customerIds = List.of(4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L);
        List<User> customers = List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9, user10);

        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(TypeBoard.HALF);
        request.setCustomerIds(customerIds);
        request.setIsGroup(true);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findAllById(any())).thenReturn(customers);

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
                .hasMessageContaining("El tipo de pensión es obligatorio");
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
        request.setCustomerIds(List.of(1L));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findAllById(any())).thenReturn(List.of(adultUser));

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
        request.setCustomerIds(List.of(1L));
        request.setIsGroup(false);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findAllById(any())).thenReturn(List.of(adultUser));

        assertThatThrownBy(() -> bookingPricingService.quote(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void quote_ShouldThrowWhenTypeBoardIsNull() {
        BookingQuoteRequestDTO request = new BookingQuoteRequestDTO();
        request.setTravelId(1L);
        request.setTypeBoard(null);
        request.setCustomerIds(List.of(1L));
        request.setIsGroup(false);

        assertThatThrownBy(() -> bookingPricingService.quote(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El tipo de pensión es obligatorio");
    }
}
