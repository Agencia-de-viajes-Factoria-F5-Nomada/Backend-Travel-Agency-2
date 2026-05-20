package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.OfferRequestDTO;
import com.inditex.g1_agencia_viajes.dto.OfferResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.OfferMapper;
import com.inditex.g1_agencia_viajes.model.Offer;
import com.inditex.g1_agencia_viajes.repository.OfferRepository;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    private OfferMapper offerMapper;

    private OfferService offerService;

    private Offer offer;

    @BeforeEach
    void setUp() {
        offerMapper = Mappers.getMapper(OfferMapper.class);
        offerService = new OfferService(offerRepository, offerMapper);

        offer = new Offer();
        offer.setOfferId(1L);
        offer.setDiscountPercentage(20.0);
        offer.setStartDate(LocalDate.of(2026, 1, 1));
        offer.setEndDate(LocalDate.of(2026, 12, 31));
    }

    @Test
    void findAll_ShouldReturnAllOffers() {
        when(offerRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(offer)));

        Page<OfferResponseDTO> result = offerService.findAll(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getOfferId()).isEqualTo(1L);
    }

    @Test
    void findById_ShouldReturnOffer() {
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        Optional<OfferResponseDTO> result = offerService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getDiscountPercentage()).isEqualTo(20.0);
    }

    @Test
    void findById_ShouldReturnEmptyOptional() {
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<OfferResponseDTO> result = offerService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldThrowWhenEndDateBeforeStartDate() {
        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setStartDate(LocalDate.of(2026, 3, 1));
        dto.setEndDate(LocalDate.of(2026, 6, 1));
        dto.setStartDate(LocalDate.of(2026, 6, 1));
        dto.setEndDate(LocalDate.of(2026, 5, 1));

        assertThatThrownBy(() -> offerService.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha de fin debe ser posterior");

        verify(offerRepository, never()).save(any());
    }

    @Test
    void save_ShouldThrowWhenEndDateEqualsStartDate() {
        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setStartDate(LocalDate.of(2026, 6, 1));
        dto.setEndDate(LocalDate.of(2026, 6, 1));

        assertThatThrownBy(() -> offerService.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha de fin debe ser posterior");

        verify(offerRepository, never()).save(any());
    }

    @Test
    void save_ShouldSaveOffer() {
        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setDiscountPercentage(20.0);
        dto.setStartDate(LocalDate.of(2026, 1, 1));
        dto.setEndDate(LocalDate.of(2026, 12, 31));

        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        OfferResponseDTO result = offerService.save(dto);

        assertThat(result).isNotNull();
        assertThat(result.getDiscountPercentage()).isEqualTo(20.0);
    }

    @Test
    void update_ShouldUpdateOffer() {
        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setDiscountPercentage(30.0);
        dto.setStartDate(LocalDate.of(2026, 3, 1));
        dto.setEndDate(LocalDate.of(2026, 6, 30));

        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        OfferResponseDTO result = offerService.update(1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getDiscountPercentage()).isEqualTo(30.0);
    }

    @Test
    void update_ShouldThrowWhenEndDateBeforeStartDate() {
        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setStartDate(LocalDate.of(2026, 6, 1));
        dto.setEndDate(LocalDate.of(2026, 5, 1));

        assertThatThrownBy(() -> offerService.update(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha de fin debe ser posterior");

        verify(offerRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowResourceNotFoundException() {
        when(offerRepository.findById(99L)).thenReturn(Optional.empty());

        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setStartDate(LocalDate.of(2026, 3, 1));
        dto.setEndDate(LocalDate.of(2026, 6, 1));
        assertThatThrownBy(() -> offerService.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hemos podido encontrar la información de la oferta");
    }

    @Test
    void deleteById_ShouldDeleteOffer() {
        when(offerRepository.existsById(1L)).thenReturn(true);

        offerService.deleteById(1L);

        verify(offerRepository).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowResourceNotFoundException() {
        when(offerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> offerService.deleteById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
