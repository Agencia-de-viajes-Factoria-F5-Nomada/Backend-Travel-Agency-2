package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.exception.MinorWithoutTutorException;
import com.inditex.g1_agencia_viajes.exception.PastTravelException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.exception.TravelNotAvailableException;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.User;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import com.inditex.g1_agencia_viajes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    public Travel resolveTravelOrThrow(Long travelId) {
        return travelRepository.findByIdForUpdate(travelId)
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", travelId));
    }

    public void validateTravelNotPast(Travel travel) {
        if (travel.getStartDate().isBefore(LocalDate.now()) || travel.getStartDate().isEqual(LocalDate.now())) {
            throw new PastTravelException(travel.getId());
        }
    }

    public void validateTravelAvailability(Travel travel, int numPassengers) {
        int availablePlaces = travel.getAvailablePlaces() == null ? 0 : travel.getAvailablePlaces();
        if (availablePlaces < numPassengers) {
            throw new TravelNotAvailableException(travel.getId());
        }
    }

    public void validateCustomers(List<User> customers) {
        if (customers == null) {
            return;
        }
        for (User customer : customers) {
            validateMinorHasTutor(customer);
        }
    }

    public void validateMinorHasTutor(User user) {
        if (user != null
                && user.getAge() != null
                && user.getAge() < 18
                && user.getTutorId() == null) {
            throw new MinorWithoutTutorException();
        }
    }

    public List<User> resolveCustomersByIds(List<Long> customerIds) {
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
