package com.inditex.g1_agencia_viajes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripSegmentRequestDTO {

    @NotNull(message = "El viaje es obligatorio")
    private Long travelId;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Bus is required")
    private Long busId;

    @NotNull(message = "Driver is required")
    private Long driverId;

    private String activityName;
}
