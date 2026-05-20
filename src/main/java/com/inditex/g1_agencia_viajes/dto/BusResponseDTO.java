package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;

@Data
public class BusResponseDTO {

    private Long id;
    private String licensePlate;
    private Integer capacity;
    private Integer availablePlaces;
    private Boolean bath;
    private Boolean wifi;
    private Boolean AC;
    private Boolean USB;
    private Boolean active;
}