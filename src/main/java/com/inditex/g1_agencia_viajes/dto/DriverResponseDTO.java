package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;

@Data
public class DriverResponseDTO {
    private Long id;
    private String name;
    private String phone;
    private Boolean licenceActive;
    private String imageUrl;
    private Long busId;
    private String busLicensePlate;
    private Boolean active;
}