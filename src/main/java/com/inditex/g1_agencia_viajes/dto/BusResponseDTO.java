package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;

import java.util.List;

@Data
public class BusResponseDTO {

    private Long id;
    private String licensePlate;
    private Integer capacity;
    private String location;
    private Integer availablePlaces;
    private Boolean bath;
    private Boolean wifi;
    private Boolean AC;
    private Boolean USB;
    private Boolean active;
    private List<BusTravelSummaryDTO> travels;
}