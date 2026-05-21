package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripSegmentResponseDTO {

    private Long id;
    private Long travelId;
    private String origin;
    private String destination;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long busId;
    private String busLicensePlate;
    private Long driverId;
    private String driverName;
    private String activityName;
}
