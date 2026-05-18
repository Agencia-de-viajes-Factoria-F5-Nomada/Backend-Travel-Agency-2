package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.OfferRequestDTO;
import com.inditex.g1_agencia_viajes.dto.OfferResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.OfferMapper;
import com.inditex.g1_agencia_viajes.model.Offer;
import com.inditex.g1_agencia_viajes.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    @Transactional(readOnly = true)
    public Page<OfferResponseDTO> findAll(Pageable pageable) {
        return offerRepository.findAll(pageable)
                .map(offerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<OfferResponseDTO> findById(Long id) {
        return offerRepository.findById(id)
                .map(offerMapper::toDTO);
    }

    @Transactional
    public OfferResponseDTO save(OfferRequestDTO dto) {
        validateDateOrder(dto);
        Offer offer = offerMapper.toEntity(dto);
        return offerMapper.toDTO(offerRepository.save(offer));
    }

    @Transactional
    public OfferResponseDTO update(Long id, OfferRequestDTO dto) {
        validateDateOrder(dto);
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("la oferta", id));
        offer.setDiscountPercentage(dto.getDiscountPercentage());
        offer.setStartDate(dto.getStartDate());
        offer.setEndDate(dto.getEndDate());
        return offerMapper.toDTO(offerRepository.save(offer));
    }

    private void validateDateOrder(OfferRequestDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate()) ||
                dto.getEndDate().isEqual(dto.getStartDate())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }
    }

    @Transactional
    public void deleteById(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new ResourceNotFoundException("la oferta", id);
        }
        offerRepository.deleteById(id);
    }
}