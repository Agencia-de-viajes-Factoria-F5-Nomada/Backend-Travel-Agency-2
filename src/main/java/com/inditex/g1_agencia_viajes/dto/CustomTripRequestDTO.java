package com.inditex.g1_agencia_viajes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomTripRequestDTO {

    @NotBlank(message = "El destino es obligatorio")
    private String customDestiny;

    private LocalDate customStartDate;

    private LocalDate customEndDate;

    @Min(value = 1, message = "Debe haber al menos 1 viajero")
    private Integer passengers;

    private Boolean includesFlight;

    private Boolean includesHotel;

    private Boolean includesActivities;

    private LocalDateTime boughtDate;

    private String typeBoard;

    private BigDecimal basePricePerPassenger;
}