package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.CustomTripRequestDTO;
import com.inditex.g1_agencia_viajes.dto.CustomTripRequestResponseDTO;
import com.inditex.g1_agencia_viajes.model.CustomTripRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring")
public interface CustomTripRequestMapper {

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "preferenceSummary", source = "customDestiny")
    CustomTripRequest toEntity(CustomTripRequestDTO dto);

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    CustomTripRequestResponseDTO toResponseDTO(CustomTripRequest entity);
}