package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.UserResponseDTO;
import com.inditex.g1_agencia_viajes.model.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingUserMapper {

    public UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        dto.setDni(user.getDni());
        return dto;
    }

    public List<UserResponseDTO> toUserResponseDTOList(List<User> users) {
        if (users == null) return null;
        return users.stream()
                .map(this::toUserResponseDTO)
                .collect(Collectors.toList());
    }
}
