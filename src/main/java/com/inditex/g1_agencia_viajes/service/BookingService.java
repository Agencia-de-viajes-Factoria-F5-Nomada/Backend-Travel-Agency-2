package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BookingUserRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BookingRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingResponseDTO;
import com.inditex.g1_agencia_viajes.exception.BusFullException;
import com.inditex.g1_agencia_viajes.exception.MinorWithoutTutorException;
import com.inditex.g1_agencia_viajes.exception.PastTravelException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.exception.TravelNotAvailableException;
import com.inditex.g1_agencia_viajes.mapper.BookingMapper;
import com.inditex.g1_agencia_viajes.model.Booking;
import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.TripSegment;
import com.inditex.g1_agencia_viajes.model.User;
import com.inditex.g1_agencia_viajes.repository.BookingRepository;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import com.inditex.g1_agencia_viajes.repository.TripSegmentRepository;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TravelRepository travelRepository;
    private final EmployeeRepository employeeRepository;
    private final HotelService hotelService;
    private final BookingPricingService bookingPricingService;
    private final BookingMapper bookingMapper;
    private final TripSegmentRepository tripSegmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<BookingResponseDTO> findAll() {
        return bookingMapper.toDTOList(bookingRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<BookingResponseDTO> findById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDTO);
    }

    @Transactional
    public BookingResponseDTO save(BookingRequestDTO dto) {
        Booking booking = new Booking();
        booking.setBoughtDate(dto.getBoughtDate());
        booking.setTypeBoard(dto.getTypeBoard());
        booking.setIsGroup(dto.getIsGroup());

        Travel travel = travelRepository.findById(dto.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", dto.getTravelId()));

        if (travel.getStartDate().isBefore(LocalDate.now()) || travel.getStartDate().isEqual(LocalDate.now())) {
            throw new PastTravelException(travel.getId());
        }

        booking.setTravel(travel);

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("el empleado", dto.getEmployeeId()));
            booking.setEmployee(employee);
        }

        List<User> resolvedCustomers = resolveCustomersByIds(dto.getCustomerIds());
        validateCustomersForBooking(resolvedCustomers);
        booking.setCustomers(resolvedCustomers);

        int numPass = resolvedCustomers.size();
        int availablePlaces = travel.getAvailablePlaces() == null ? 0 : travel.getAvailablePlaces();
        if (availablePlaces < numPass) {
            throw new TravelNotAvailableException(travel.getId());
        }

        int totalPassengers = bookingRepository.countTotalPassengersByTravelId(travel.getId()) + numPass;
        List<TripSegment> segments = tripSegmentRepository.findByTravelId(travel.getId());
        Set<Long> seenBusIds = new HashSet<>();
        for (TripSegment segment : segments) {
            Bus bus = segment.getBus();
            if (bus != null && seenBusIds.add(bus.getId()) && bus.getCapacity() < totalPassengers) {
                throw new BusFullException(bus.getId(), bus.getLicensePlate());
            }
        }

        if (travel.getHotel() != null) {
            hotelService.reduceCapacity(travel.getHotel().getId(), numPass);
        }
        travel.setAvailablePlaces(availablePlaces - numPass);
        travelRepository.save(travel);

        booking.setTotalPrice(bookingPricingService.calculateTotalPrice(booking));
        Booking saved = bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingCreatedEvent(saved.getBookingId()));
        return bookingMapper.toDTO(saved);
    }

    @Transactional
    public BookingResponseDTO update(Long id, BookingRequestDTO dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("la reserva", id));

        Travel oldTravel = booking.getTravel();
        int oldPassengerCount = booking.getCustomers() != null ? booking.getCustomers().size() : 0;

        if (oldTravel != null) {
            oldTravel.setAvailablePlaces(
                    (oldTravel.getAvailablePlaces() == null ? 0 : oldTravel.getAvailablePlaces()) + oldPassengerCount);
            travelRepository.save(oldTravel);
            if (oldTravel.getHotel() != null) {
                hotelService.releaseCapacity(oldTravel.getHotel().getId(), oldPassengerCount);
            }
        }

        booking.setBoughtDate(dto.getBoughtDate());
        booking.setTypeBoard(dto.getTypeBoard());
        booking.setIsGroup(dto.getIsGroup());

        Travel newTravel = travelRepository.findById(dto.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", dto.getTravelId()));
        if (newTravel.getStartDate().isBefore(LocalDate.now()) || newTravel.getStartDate().isEqual(LocalDate.now())) {
            throw new PastTravelException(newTravel.getId());
        }
        booking.setTravel(newTravel);

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("el empleado", dto.getEmployeeId()));
            booking.setEmployee(employee);
        }

        List<User> resolvedCustomers = resolveCustomersByIds(dto.getCustomerIds());
        validateCustomersForBooking(resolvedCustomers);
        booking.setCustomers(resolvedCustomers);
        int newPassengerCount = resolvedCustomers.size();

        int availablePlaces = newTravel.getAvailablePlaces() == null ? 0 : newTravel.getAvailablePlaces();
        if (availablePlaces < newPassengerCount) {
            throw new TravelNotAvailableException(newTravel.getId());
        }

        int totalPassengers = bookingRepository.countTotalPassengersByTravelId(newTravel.getId())
                - oldPassengerCount + newPassengerCount;
        List<TripSegment> segments = tripSegmentRepository.findByTravelId(newTravel.getId());
        Set<Long> seenBusIds = new HashSet<>();
        for (TripSegment segment : segments) {
            Bus bus = segment.getBus();
            if (bus != null && seenBusIds.add(bus.getId()) && bus.getCapacity() < totalPassengers) {
                throw new BusFullException(bus.getId(), bus.getLicensePlate());
            }
        }

        if (newTravel.getHotel() != null) {
            hotelService.reduceCapacity(newTravel.getHotel().getId(), newPassengerCount);
        }
        newTravel.setAvailablePlaces(availablePlaces - newPassengerCount);
        travelRepository.save(newTravel);

        booking.setTotalPrice(bookingPricingService.calculateTotalPrice(booking));
        Booking saved = bookingRepository.save(booking);
        eventPublisher.publishEvent(new BookingCreatedEvent(saved.getBookingId()));
        return bookingMapper.toDTO(saved);
    }

    @Transactional
    public void deleteById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("la reserva", id));

        if (booking.getTravel() != null && booking.getTravel().getHotel() != null) {
            hotelService.releaseCapacity(booking.getTravel().getHotel().getId(), booking.getCustomers().size());
        }

        Travel travel = booking.getTravel();
        if (travel != null) {
            int availablePlaces = travel.getAvailablePlaces() == null ? 0 : travel.getAvailablePlaces();
            travel.setAvailablePlaces(availablePlaces + booking.getCustomers().size());
            travelRepository.save(travel);
        }

        bookingRepository.deleteById(id);
    }

    @Transactional
    public void addCustomerToBooking(BookingUserRequestDTO request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("la reserva", request.getBookingId()));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("el usuario", request.getUserId()));
        validateMinorHasTutor(user);

        Travel travel = booking.getTravel();
        if (travel != null && (travel.getStartDate().isBefore(LocalDate.now()) || travel.getStartDate().isEqual(LocalDate.now()))) {
            throw new PastTravelException(travel.getId());
        }
        if (travel != null) {
            int totalPassengers = bookingRepository.countTotalPassengersByTravelId(travel.getId()) + 1;
            List<TripSegment> segments = tripSegmentRepository.findByTravelId(travel.getId());
            Set<Long> seenBusIds = new HashSet<>();
            for (TripSegment segment : segments) {
                Bus bus = segment.getBus();
                if (bus != null && seenBusIds.add(bus.getId()) && bus.getCapacity() < totalPassengers) {
                    throw new BusFullException(bus.getId(), bus.getLicensePlate());
                }
            }
        }
        if (travel != null && travel.getHotel() != null) {
            hotelService.reduceCapacity(travel.getHotel().getId(), 1);
        }
        if (travel != null) {
            int availablePlaces = travel.getAvailablePlaces() == null ? 0 : travel.getAvailablePlaces();
            if (availablePlaces < 1) {
                throw new TravelNotAvailableException(travel.getId());
            }
            travel.setAvailablePlaces(availablePlaces - 1);
            travelRepository.save(travel);
        }

        booking.getCustomers().add(user);
        booking.setTotalPrice(bookingPricingService.calculateTotalPrice(booking));
        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public BookingQuoteResponseDTO quote(BookingQuoteRequestDTO request) {
        return bookingPricingService.quote(request);
    }

    private void validateCustomersForBooking(List<User> customers) {
        if (customers == null) {
            return;
        }

        for (User customer : customers) {
            validateMinorHasTutor(customer);
        }
    }

    private void validateMinorHasTutor(User user) {
        if (user != null
                && user.getAge() != null
                && user.getAge() < 18
                && user.getTutorId() == null) {
            throw new MinorWithoutTutorException();
        }
    }

    private List<User> resolveCustomersByIds(List<Long> customerIds) {
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
}
