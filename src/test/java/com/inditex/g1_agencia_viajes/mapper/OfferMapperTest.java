package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.OfferRequestDTO;
import com.inditex.g1_agencia_viajes.dto.OfferResponseDTO;
import com.inditex.g1_agencia_viajes.model.Offer;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OfferMapperTest {

    private final OfferMapper mapper = Mappers.getMapper(OfferMapper.class);

    @Test
    void toDTO_ShouldMapAllFields() {
        Offer offer = new Offer();
        offer.setOfferId(1L);
        offer.setDiscountPercentage(15.0);
        offer.setStartDate(LocalDate.of(2026, 1, 1));
        offer.setEndDate(LocalDate.of(2026, 12, 31));

        OfferResponseDTO dto = mapper.toDTO(offer);

        assertThat(dto.getOfferId()).isEqualTo(1L);
        assertThat(dto.getDiscountPercentage()).isEqualTo(15.0);
        assertThat(dto.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(dto.getEndDate()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setDiscountPercentage(10.0);
        dto.setStartDate(LocalDate.of(2026, 6, 1));
        dto.setEndDate(LocalDate.of(2026, 9, 30));

        Offer offer = mapper.toEntity(dto);

        assertThat(offer.getDiscountPercentage()).isEqualTo(10.0);
        assertThat(offer.getStartDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(offer.getEndDate()).isEqualTo(LocalDate.of(2026, 9, 30));
    }

    @Test
    void updateFromDto_ShouldUpdateNonNullFields() {
        Offer offer = new Offer();
        offer.setDiscountPercentage(5.0);
        offer.setStartDate(LocalDate.of(2026, 1, 1));

        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setDiscountPercentage(20.0);

        mapper.updateFromDto(dto, offer);

        assertThat(offer.getDiscountPercentage()).isEqualTo(20.0);
        assertThat(offer.getStartDate()).isEqualTo(LocalDate.of(2026, 1, 1));
    }
}
