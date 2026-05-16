package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.DashboardResponseDTO;
import com.inditex.g1_agencia_viajes.repository.BookingRepository;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class DashboardService {

    private final TravelRepository travelRepository;
    private final BookingRepository bookingRepository;

    public DashboardService(TravelRepository travelRepository, BookingRepository bookingRepository) {
        this.travelRepository = travelRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboard() {
        DashboardResponseDTO dto = new DashboardResponseDTO();

        dto.setTravelsPerYear(buildTravelsPerYear());
        dto.setCurrentYearEarnings(getCurrentYearEarnings());
        dto.setTopTravels(buildTopTravels());

        return dto;
    }

    private Map<Integer, Long> buildTravelsPerYear() {
        List<Object[]> results = travelRepository.countTravelsPerYear();
        Map<Integer, Long> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            Integer year = ((Number) row[0]).intValue();
            Long count = ((Number) row[1]).longValue();
            map.put(year, count);
        }
        return map;
    }

    private Double getCurrentYearEarnings() {
        int currentYear = LocalDate.now().getYear();
        Double earnings = bookingRepository.sumEarningsByTravelYear(currentYear);
        return earnings != null ? earnings : 0.0;
    }

    private List<DashboardResponseDTO.TopTravelDTO> buildTopTravels() {
        int currentYear = LocalDate.now().getYear();
        List<Object[]> results = bookingRepository.findTopTravelsByRevenue(currentYear);
        List<DashboardResponseDTO.TopTravelDTO> list = new ArrayList<>();
        for (Object[] row : results) {
            DashboardResponseDTO.TopTravelDTO top = new DashboardResponseDTO.TopTravelDTO();
            top.setTravelId(((Number) row[0]).longValue());
            top.setDestiny((String) row[1]);
            top.setRevenue(((Number) row[2]).doubleValue());
            list.add(top);
        }
        return list;
    }
}
