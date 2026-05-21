package com.inditex.g1_agencia_viajes.dto;

import com.inditex.g1_agencia_viajes.model.OfferType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferResponseDTO {

    private Long offerId;
    private Double discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;

    private OfferType offerType;
}
