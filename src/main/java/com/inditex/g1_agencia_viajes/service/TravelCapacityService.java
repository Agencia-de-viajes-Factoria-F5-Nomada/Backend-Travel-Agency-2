package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TravelCapacityService {

    private final TravelRepository travelRepository;
    private final HotelService hotelService;

    @Transactional
    public void occupyCapacity(Travel travel, int numPassengers) {
        if (travel.getHotel() != null) {
            hotelService.reduceCapacity(travel.getHotel().getId(), numPassengers);
        }
        int availablePlaces = travel.getAvailablePlaces() == null ? 0 : travel.getAvailablePlaces();
        travel.setAvailablePlaces(availablePlaces - numPassengers);
        travelRepository.save(travel);
    }

    @Transactional
    public void releaseCapacity(Travel travel, int numPassengers) {
        if (travel.getHotel() != null) {
            hotelService.releaseCapacity(travel.getHotel().getId(), numPassengers);
        }
        int availablePlaces = travel.getAvailablePlaces() == null ? 0 : travel.getAvailablePlaces();
        travel.setAvailablePlaces(availablePlaces + numPassengers);
        travelRepository.save(travel);
    }
}
