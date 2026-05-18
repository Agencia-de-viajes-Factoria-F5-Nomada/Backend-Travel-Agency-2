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
        Offer offer = offerMapper.toEntity(dto);
        return offerMapper.toDTO(offerRepository.save(offer));
    }

    @Transactional
    public OfferResponseDTO update(Long id, OfferRequestDTO dto) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("la oferta", id));
        offerMapper.updateFromDto(dto, offer);
        return offerMapper.toDTO(offerRepository.save(offer));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new ResourceNotFoundException("la oferta", id);
        }
        offerRepository.deleteById(id);
    }
}