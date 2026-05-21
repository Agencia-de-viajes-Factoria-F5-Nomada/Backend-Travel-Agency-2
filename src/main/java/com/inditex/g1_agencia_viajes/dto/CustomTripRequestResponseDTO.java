package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomTripRequestResponseDTO {

    private Long id;
    private String preferenceSummary;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer passengers;
    private Boolean includesFlight;
    private Boolean includesHotel;
    private Boolean includesActivities;
    private LocalDateTime boughtDate;
    private Boolean isGroup;
    private Long userId;
    private String status;
    private String typeBoard;
    private BigDecimal basePricePerPassenger;
    private BigDecimal totalBeforeDiscount;
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;
}