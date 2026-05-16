package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardResponseDTO {
    private Map<Integer, Long> travelsPerYear;
    private Double currentYearEarnings;
    private List<TopTravelDTO> topTravels;

    @Data
    public static class TopTravelDTO {
        private Long travelId;
        private String destiny;
        private Double revenue;
    }
}
