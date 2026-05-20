package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingUserRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BookingRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingResponseDTO;
import com.inditex.g1_agencia_viajes.event.BookingCreatedEvent;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.BookingMapper;
import com.inditex.g1_agencia_viajes.model.Booking;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.User;
import com.inditex.g1_agencia_viajes.repository.BookingRepository;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final BookingPricingService bookingPricingService;
    private final BookingMapper bookingMapper;
    private final ApplicationEventPublisher eventPublisher;

    private final BookingValidator bookingValidator;
    private final TravelCapacityService travelCapacityService;

    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(bookingMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<BookingResponseDTO> findById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDTO);
    }

    @Transactional
    public BookingResponseDTO save(BookingRequestDTO dto) {
        Booking booking = new Booking();
        booking.setBoughtDate(dto.getBoughtDate()!= null ? dto.getBoughtDate() : LocalDate.now().atStartOfDay());
        booking.setTypeBoard(dto.getTypeBoard());
        booking.setIsGroup(dto.getIsGroup());

        Travel travel = bookingValidator.resolveTravelOrThrow(dto.getTravelId());
        bookingValidator.validateTravelNotPast(travel);
        booking.setTravel(travel);
        resolveAndSetEmployee(booking, dto.getEmployeeId());

        List<User> customers = bookingValidator.resolveCustomersByIds(dto.getCustomerIds());
        bookingValidator.validateCustomers(customers);
        booking.setCustomers(customers);

        int numPassengers = customers.size();
        bookingValidator.validateTravelAvailability(travel, numPassengers);

        travelCapacityService.occupyCapacity(travel, numPassengers);

        return finalizeBooking(booking);
    }

    @Transactional
    public BookingResponseDTO update(Long id, BookingRequestDTO dto) {
        Booking booking = findBookingOrThrow(id);

        Travel oldTravel = booking.getTravel();
        int oldPassengerCount = getPassengerCount(booking);
        if (oldTravel != null) {
            travelCapacityService.releaseCapacity(oldTravel, oldPassengerCount);
        }

        booking.setBoughtDate(dto.getBoughtDate());
        booking.setTypeBoard(dto.getTypeBoard());
        booking.setIsGroup(dto.getIsGroup());

        Travel newTravel = bookingValidator.resolveTravelOrThrow(dto.getTravelId());
        bookingValidator.validateTravelNotPast(newTravel);
        booking.setTravel(newTravel);

        resolveAndSetEmployee(booking, dto.getEmployeeId());

        List<User> customers = bookingValidator.resolveCustomersByIds(dto.getCustomerIds());
        bookingValidator.validateCustomers(customers);
        booking.setCustomers(customers);

        int newPassengerCount = customers.size();
        bookingValidator.validateTravelAvailability(newTravel, newPassengerCount);

        travelCapacityService.occupyCapacity(newTravel, newPassengerCount);

        return finalizeBooking(booking);
    }

    @Transactional
    public void deleteById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("la reserva", id));

        Travel travel = booking.getTravel();
        if (travel != null) {
            travelCapacityService.releaseCapacity(travel, booking.getCustomers().size());
        }

        bookingRepository.deleteById(id);
    }

    @Transactional
    public void addCustomerToBooking(BookingUserRequestDTO request) {
        Booking booking = findBookingOrThrow(request.getBookingId());
        User user = findUserOrThrow(request.getUserId());
        bookingValidator.validateMinorHasTutor(user);

        Travel travel = booking.getTravel();
        if (travel == null) {
            return;
        }

        bookingValidator.validateTravelNotPast(travel);
        bookingValidator.validateTravelAvailability(travel, 1);

        travelCapacityService.occupyCapacity(travel, 1);

        booking.getCustomers().add(user);
        booking.setTotalPrice(bookingPricingService.calculateTotalPrice(booking));
        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public BookingQuoteResponseDTO quote(BookingQuoteRequestDTO request) {
        return bookingPricingService.quote(request);
    }

    private Booking findBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("la reserva", id));
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el usuario", id));
    }

    private void resolveAndSetEmployee(Booking booking, Long employeeId) {
        if (employeeId != null) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("el empleado", employeeId));
            booking.setEmployee(employee);
        }
    }

    private int getPassengerCount(Booking booking) {
        return booking.getCustomers() != null ? booking.getCustomers().size() : 0;
    }

    private BookingResponseDTO finalizeBooking(Booking booking) {
        booking.setTotalPrice(bookingPricingService.calculateTotalPrice(booking));
        Booking saved = bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingCreatedEvent(saved.getBookingId()));
        return bookingMapper.toDTO(saved);
    }
}
