package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.UserResponseDTO;
import com.inditex.g1_agencia_viajes.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingUserMapper {

    @Mapping(target = "tutorId", ignore = true)
    @Mapping(target = "dni", ignore = true)
    @Mapping(target = "passport", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "active", ignore = true)
    UserResponseDTO toUserResponseDTO(User user);

    List<UserResponseDTO> toUserResponseDTOList(List<User> users);
}
