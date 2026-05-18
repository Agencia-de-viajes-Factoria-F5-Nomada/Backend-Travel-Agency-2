package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.OfferRequestDTO;
import com.inditex.g1_agencia_viajes.dto.OfferResponseDTO;
import com.inditex.g1_agencia_viajes.model.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    Offer toEntity(OfferRequestDTO dto);

    OfferResponseDTO toDTO(Offer offer);

    void updateFromDto(OfferRequestDTO dto, @MappingTarget Offer offer);
}
