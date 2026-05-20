package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.DriverRequestDTO;
import com.inditex.g1_agencia_viajes.dto.DriverResponseDTO;
import com.inditex.g1_agencia_viajes.model.Driver;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "bus", ignore = true)
    Driver toEntity(DriverRequestDTO dto);

    @Mapping(target = "busId", source = "bus.id")
    @Mapping(target = "busLicensePlate", source = "bus.licensePlate")
    DriverResponseDTO toDTO(Driver driver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "bus", ignore = true)
    void updateFromDto(DriverRequestDTO dto, @MappingTarget Driver driver);
}
