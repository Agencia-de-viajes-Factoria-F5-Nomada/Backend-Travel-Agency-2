package com.inditex.gym_lorza.mapper;

import com.inditex.gym_lorza.dto.UserRequestDTO;
import com.inditex.gym_lorza.dto.UserResponseDTO;
import com.inditex.gym_lorza.model.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setDni(dto.getDni());
        user.setStartYear(dto.getStartYear());
        user.setIsActive(dto.getIsActive());
        user.setImage(dto.getImage());
        return user;
    }

    public static UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setDni(user.getDni());
        dto.setStartYear(user.getStartYear());
        dto.setIsActive(user.getIsActive());
        dto.setImage(user.getImage());
        return dto;
    }
}