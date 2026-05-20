package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BusTravelSummaryDTO {
    private Long travelId;
    private String destiny;
    private LocalDate startDate;
    private LocalDate endDate;
}
