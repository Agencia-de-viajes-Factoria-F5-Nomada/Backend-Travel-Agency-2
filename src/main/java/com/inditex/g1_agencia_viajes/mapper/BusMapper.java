package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import com.inditex.g1_agencia_viajes.model.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BusMapper {

    Bus toEntity(BusRequestDTO dto);

    BusResponseDTO toDTO(Bus bus);

    void updateFromDto(BusRequestDTO dto, @MappingTarget Bus bus);
}
