package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.UserRequestDTO;
import com.inditex.g1_agencia_viajes.dto.UserResponseDTO;
import com.inditex.g1_agencia_viajes.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "tutorId", ignore = true)
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "tutorId", source = "tutorId.id")
    UserResponseDTO toDTO(User user);

    @Mapping(target = "tutorId", ignore = true)
    void updateFromDto(UserRequestDTO dto, @MappingTarget User user);
}
