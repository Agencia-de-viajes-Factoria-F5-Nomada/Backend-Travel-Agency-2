package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.TripSegment;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import com.inditex.g1_agencia_viajes.repository.TripSegmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TravelCapacityService {

    private final TravelRepository travelRepository;
    private final HotelService hotelService;
    private final BusService busService;
    private final TripSegmentRepository tripSegmentRepository;

    @Transactional
    public void occupyCapacity(Travel travel, int numPassengers) {
        if (travel.getHotel() != null) {
            hotelService.reduceCapacity(travel.getHotel().getId(), numPassengers);
        }
        reduceBusCapacity(travel.getId(), numPassengers);
        int availablePlaces = travel.getAvailablePlaces() == null ? 0 : travel.getAvailablePlaces();
        travel.setAvailablePlaces(availablePlaces - numPassengers);
        travelRepository.save(travel);
    }

    @Transactional
    public void releaseCapacity(Travel travel, int numPassengers) {
        if (travel.getHotel() != null) {
            hotelService.releaseCapacity(travel.getHotel().getId(), numPassengers);
        }
        releaseBusCapacity(travel.getId(), numPassengers);
        int availablePlaces = travel.getAvailablePlaces() == null ? 0 : travel.getAvailablePlaces();
        travel.setAvailablePlaces(availablePlaces + numPassengers);
        travelRepository.save(travel);
    }

    private void reduceBusCapacity(Long travelId, int numPassengers) {
        List<TripSegment> segments = tripSegmentRepository.findByTravelId(travelId);
        Set<Long> seenBusIds = new HashSet<>();
        for (TripSegment segment : segments) {
            Bus bus = segment.getBus();
            if (bus != null && seenBusIds.add(bus.getId())) {
                busService.reduceCapacity(bus.getId(), numPassengers);
            }
        }
    }

    private void releaseBusCapacity(Long travelId, int numPassengers) {
        List<TripSegment> segments = tripSegmentRepository.findByTravelId(travelId);
        Set<Long> seenBusIds = new HashSet<>();
        for (TripSegment segment : segments) {
            Bus bus = segment.getBus();
            if (bus != null && seenBusIds.add(bus.getId())) {
                busService.releaseCapacity(bus.getId(), numPassengers);
            }
        }
    }
}
