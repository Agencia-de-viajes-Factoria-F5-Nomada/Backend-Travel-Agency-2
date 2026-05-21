package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.CustomTripRequestDTO;
import com.inditex.g1_agencia_viajes.dto.CustomTripRequestResponseDTO;
import com.inditex.g1_agencia_viajes.event.CustomTripCreatedEvent;
import com.inditex.g1_agencia_viajes.mapper.CustomTripRequestMapper;
import com.inditex.g1_agencia_viajes.model.CustomTripRequest;
import com.inditex.g1_agencia_viajes.repository.CustomTripRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CustomTripRequestService {

    private final CustomTripRequestRepository customTripRequestRepository;
    private final CustomTripRequestMapper customTripRequestMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CustomTripRequestResponseDTO save(CustomTripRequestDTO dto, Long userId) {
        CustomTripRequest entity = customTripRequestMapper.toEntity(dto);
        entity.setUserId(userId);
        entity.setIsGroup(entity.getPassengers() != null && entity.getPassengers() >= 10);
        entity.setStatus("PENDING");
        if (entity.getBoughtDate() == null) {
            entity.setBoughtDate(java.time.LocalDateTime.now());
        }

        calculatePricing(entity);

        CustomTripRequest saved = customTripRequestRepository.save(entity);
        eventPublisher.publishEvent(new CustomTripCreatedEvent(saved.getId()));
        return customTripRequestMapper.toResponseDTO(saved);
    }

    private void calculatePricing(CustomTripRequest entity) {
        BigDecimal basePrice = entity.getBasePricePerPassenger() != null
                ? entity.getBasePricePerPassenger()
                : BigDecimal.ZERO;

        int passengers = entity.getPassengers() != null ? entity.getPassengers() : 1;
        BigDecimal totalBeforeDiscount = basePrice.multiply(BigDecimal.valueOf(passengers));
        BigDecimal totalDiscount = BigDecimal.ZERO;

        if (Boolean.TRUE.equals(entity.getIsGroup()) && passengers >= 10) {
            totalDiscount = totalBeforeDiscount.multiply(BigDecimal.valueOf(0.05))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalPrice = totalBeforeDiscount.subtract(totalDiscount);

        entity.setTotalBeforeDiscount(totalBeforeDiscount.setScale(2, RoundingMode.HALF_UP));
        entity.setTotalDiscount(totalDiscount);
        entity.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
    }
}