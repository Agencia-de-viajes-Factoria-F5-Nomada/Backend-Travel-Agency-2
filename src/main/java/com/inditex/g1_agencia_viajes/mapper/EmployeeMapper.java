package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.EmployeeRequestDTO;
import com.inditex.g1_agencia_viajes.dto.EmployeeResponseDTO;
import com.inditex.g1_agencia_viajes.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "password", ignore = true)
    Employee toEntity(EmployeeRequestDTO dto);

    EmployeeResponseDTO toDTO(Employee employee);

    @Mapping(target = "password", ignore = true)
    void updateFromDto(EmployeeRequestDTO dto, @MappingTarget Employee employee);
}
