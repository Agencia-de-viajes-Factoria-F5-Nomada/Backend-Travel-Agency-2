package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.DriverRequestDTO;
import com.inditex.g1_agencia_viajes.dto.DriverResponseDTO;
import com.inditex.g1_agencia_viajes.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    Driver toEntity(DriverRequestDTO dto);

    DriverResponseDTO toDTO(Driver driver);

    void updateFromDto(DriverRequestDTO dto, @MappingTarget Driver driver);
}
