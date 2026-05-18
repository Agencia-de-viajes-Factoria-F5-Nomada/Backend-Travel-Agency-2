package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.HotelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.HotelResponseDTO;
import com.inditex.g1_agencia_viajes.model.Hotel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    Hotel toEntity(HotelRequestDTO dto);

    HotelResponseDTO toDTO(Hotel hotel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(HotelRequestDTO dto, @MappingTarget Hotel hotel);
}
