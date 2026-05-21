package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.OfferRequestDTO;
import com.inditex.g1_agencia_viajes.dto.OfferResponseDTO;
import com.inditex.g1_agencia_viajes.model.Offer;
import com.inditex.g1_agencia_viajes.model.OfferType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    Offer toEntity(OfferRequestDTO dto);

    OfferResponseDTO toDTO(Offer offer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(OfferRequestDTO dto, @MappingTarget Offer offer);
}
