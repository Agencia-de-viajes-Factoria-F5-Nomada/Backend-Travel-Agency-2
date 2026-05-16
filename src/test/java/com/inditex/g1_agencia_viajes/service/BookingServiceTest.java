package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BookingRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BookingUserRequestDTO;
import com.inditex.g1_agencia_viajes.exception.MinorWithoutTutorException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.exception.TravelNotAvailableException;
import com.inditex.g1_agencia_viajes.mapper.BookingMapper;
import com.inditex.g1_agencia_viajes.model.*;
import com.inditex.g1_agencia_viajes.repository.BookingRepository;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import com.inditex.g1_agencia_viajes.repository.TripSegmentRepository;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private HotelService hotelService;

    @Mock
    private BookingPricingService bookingPricingService;

    @Mock
    private TripSegmentRepository tripSegmentRepository;

    private BookingMapper bookingMapper;

    private BookingService bookingService;

    private Booking booking;
    private User adultUser;
    private User secondAdultUser;
    private User minorWithoutTutor;
    private Travel travel;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper();
        bookingService = new BookingService(bookingRepository, userRepository, travelRepository,
                employeeRepository, hotelService, bookingPricingService, bookingMapper, tripSegmentRepository);

        travel = new Travel();
        travel.setId(1L);
        travel.setDestiny("Paris");
        travel.setStartDate(LocalDate.now().plusDays(30));
        travel.setEndDate(LocalDate.now().plusDays(35));
        travel.setAvailablePlaces(10);

        adultUser = new User();
        adultUser.setId(2L);
        adultUser.setName("Adult");
        adultUser.setSurname("User");
        adultUser.setAge(25);
        adultUser.setEmail("adult@test.com");

        secondAdultUser = new User();
        secondAdultUser.setId(3L);
        secondAdultUser.setName("Adult2");
        secondAdultUser.setSurname("User2");
        secondAdultUser.setAge(30);
        secondAdultUser.setEmail("adult2@test.com");

        minorWithoutTutor = new User();
        minorWithoutTutor.setId(4L);
        minorWithoutTutor.setName("Minor");
        minorWithoutTutor.setSurname("NoTutor");
        minorWithoutTutor.setAge(16);
        minorWithoutTutor.setEmail("minornt@test.com");

        booking = new Booking();
        booking.setBookingId(1L);
        booking.setBoughtDate(LocalDateTime.now());
        booking.setTypeBoard(TypeBoard.HALF);
        booking.setIsGroup(false);
        booking.setTotalPrice(500.0);
        booking.setTravel(travel);
        booking.setCustomers(new ArrayList<>());
    }

    @Test
    void findAll_ShouldReturnAllBookings() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingResponseDTO> result = bookingService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookingId()).isEqualTo(1L);
    }

    @Test
    void findById_ShouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Optional<BookingResponseDTO> result = bookingService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getBookingId()).isEqualTo(1L);
    }

    @Test
    void findById_ShouldReturnEmptyOptional() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<BookingResponseDTO> result = bookingService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldSaveBookingWithValidCustomers() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setTypeBoard(TypeBoard.HALF);
        dto.setTravelId(1L);
        dto.setCustomerIds(List.of(2L));

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adultUser));
        when(bookingRepository.countTotalPassengersByTravelId(1L)).thenReturn(0);
        when(tripSegmentRepository.findByTravelId(1L)).thenReturn(Collections.emptyList());
        when(bookingPricingService.calculateTotalPrice(any(Booking.class))).thenReturn(500.0);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDTO result = bookingService.save(dto);

        assertThat(result).isNotNull();
        verify(travelRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(bookingPricingService).calculateTotalPrice(any(Booking.class));
    }

    @Test
    void save_ShouldReduceTravelAndHotelCapacity() {
        Hotel hotel = new Hotel();
        hotel.setId(5L);
        hotel.setAvailablePlaces(8);
        travel.setHotel(hotel);

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setTypeBoard(TypeBoard.HALF);
        dto.setTravelId(1L);
        dto.setCustomerIds(List.of(2L, 3L));

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adultUser));
        when(userRepository.findById(3L)).thenReturn(Optional.of(secondAdultUser));
        when(bookingRepository.countTotalPassengersByTravelId(1L)).thenReturn(0);
        when(tripSegmentRepository.findByTravelId(1L)).thenReturn(Collections.emptyList());
        when(bookingPricingService.calculateTotalPrice(any(Booking.class))).thenReturn(700.0);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.save(dto);

        verify(hotelService).reducirPlazas(5L, 2);
        verify(travelRepository).save(travel);
        assertThat(travel.getAvailablePlaces()).isEqualTo(8);
    }

    @Test
    void save_ShouldThrowWhenTravelHasNoEnoughPlaces() {
        travel.setAvailablePlaces(0);

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setTypeBoard(TypeBoard.HALF);
        dto.setTravelId(1L);
        dto.setCustomerIds(List.of(2L));

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adultUser));

        assertThatThrownBy(() -> bookingService.save(dto))
                .isInstanceOf(TravelNotAvailableException.class)
                .hasMessage("El viaje con id: 1 no tiene plazas disponibles");
        verifyNoInteractions(hotelService);
    }

    @Test
    void save_ShouldThrowMinorWithoutTutorException() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setTypeBoard(TypeBoard.HALF);
        dto.setTravelId(1L);
        dto.setCustomerIds(List.of(4L));

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(userRepository.findById(4L)).thenReturn(Optional.of(minorWithoutTutor));

        assertThatThrownBy(() -> bookingService.save(dto))
                .isInstanceOf(MinorWithoutTutorException.class);
    }

    @Test
    void update_ShouldUpdateBooking() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setBoughtDate(LocalDateTime.now());
        dto.setTypeBoard(TypeBoard.FULL);
        dto.setIsGroup(true);
        dto.setTravelId(2L);
        dto.setCustomerIds(List.of(2L));

        Travel newTravel = new Travel();
        newTravel.setId(2L);
        newTravel.setStartDate(LocalDate.now().plusDays(30));
        newTravel.setEndDate(LocalDate.now().plusDays(35));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adultUser));
        when(travelRepository.findById(2L)).thenReturn(Optional.of(newTravel));
        when(bookingPricingService.calculateTotalPrice(any(Booking.class))).thenReturn(800.0);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDTO result = bookingService.update(1L, dto);

        assertThat(result).isNotNull();
    }

    @Test
    void update_ShouldThrowResourceNotFoundException() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        BookingRequestDTO dto = new BookingRequestDTO();
        assertThatThrownBy(() -> bookingService.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_ShouldDeleteBooking() {
        booking.getCustomers().add(adultUser);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteById(1L);

        verify(hotelService, never()).liberarPlazas(anyLong(), anyInt());
        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void deleteById_ShouldRestoreCapacity() {
        Hotel hotel = new Hotel();
        hotel.setId(5L);
        hotel.setAvailablePlaces(6);
        travel.setHotel(hotel);
        booking.getCustomers().add(adultUser);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteById(1L);

        verify(hotelService).liberarPlazas(5L, 1);
        verify(travelRepository).save(travel);
        assertThat(travel.getAvailablePlaces()).isEqualTo(11);
    }

    @Test
    void deleteById_ShouldThrowResourceNotFoundException() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.deleteById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addCustomerToBooking_ShouldAddCustomer() {
        BookingUserRequestDTO request = new BookingUserRequestDTO();
        request.setBookingId(1L);
        request.setUserId(2L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adultUser));
        when(bookingRepository.countTotalPassengersByTravelId(1L)).thenReturn(0);
        when(tripSegmentRepository.findByTravelId(1L)).thenReturn(Collections.emptyList());
        when(bookingPricingService.calculateTotalPrice(booking)).thenReturn(600.0);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingService.addCustomerToBooking(request);

        assertThat(booking.getCustomers()).contains(adultUser);
    }

    @Test
    void addCustomerToBooking_ShouldReduceCapacity() {
        Hotel hotel = new Hotel();
        hotel.setId(5L);
        hotel.setAvailablePlaces(8);
        travel.setHotel(hotel);

        BookingUserRequestDTO request = new BookingUserRequestDTO();
        request.setBookingId(1L);
        request.setUserId(2L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(adultUser));
        when(bookingRepository.countTotalPassengersByTravelId(1L)).thenReturn(0);
        when(tripSegmentRepository.findByTravelId(1L)).thenReturn(Collections.emptyList());
        when(bookingPricingService.calculateTotalPrice(booking)).thenReturn(600.0);
        when(bookingRepository.save(booking)).thenReturn(booking);

        bookingService.addCustomerToBooking(request);

        verify(hotelService).reducirPlazas(5L, 1);
        verify(travelRepository).save(travel);
        assertThat(travel.getAvailablePlaces()).isEqualTo(9);
    }

    @Test
    void addCustomerToBooking_ShouldThrowWhenBookingNotFound() {
        BookingUserRequestDTO request = new BookingUserRequestDTO();
        request.setBookingId(99L);
        request.setUserId(2L);

        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.addCustomerToBooking(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addCustomerToBooking_ShouldThrowWhenUserNotFound() {
        BookingUserRequestDTO request = new BookingUserRequestDTO();
        request.setBookingId(1L);
        request.setUserId(99L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.addCustomerToBooking(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addCustomerToBooking_ShouldThrowMinorWithoutTutor() {
        BookingUserRequestDTO request = new BookingUserRequestDTO();
        request.setBookingId(1L);
        request.setUserId(4L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(4L)).thenReturn(Optional.of(minorWithoutTutor));

        assertThatThrownBy(() -> bookingService.addCustomerToBooking(request))
                .isInstanceOf(MinorWithoutTutorException.class);
    }

    @Test
    void quote_ShouldReturnQuote() {
        BookingQuoteRequestDTO quoteRequest = new BookingQuoteRequestDTO();
        BookingQuoteResponseDTO quoteResponse = new BookingQuoteResponseDTO();

        when(bookingPricingService.quote(quoteRequest)).thenReturn(quoteResponse);

        BookingQuoteResponseDTO result = bookingService.quote(quoteRequest);

        assertThat(result).isEqualTo(quoteResponse);
    }
}
