package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.UserRequestDTO;
import com.inditex.g1_agencia_viajes.dto.UserResponseDTO;
import com.inditex.g1_agencia_viajes.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDTO_WithTutor_ShouldMapTutorId() {
        User tutor = new User();
        tutor.setId(1L);

        User user = new User();
        user.setId(2L);
        user.setName("Ana");
        user.setSurname("García");
        user.setEmail("ana@test.com");
        user.setDni("12345678Z");
        user.setPassport("ABC123456");
        user.setAge(25);
        user.setTutorId(tutor);
        user.setActive(true);

        UserResponseDTO dto = mapper.toDTO(user);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Ana");
        assertThat(dto.getSurname()).isEqualTo("García");
        assertThat(dto.getEmail()).isEqualTo("ana@test.com");
        assertThat(dto.getDni()).isEqualTo("12345678Z");
        assertThat(dto.getPassport()).isEqualTo("ABC123456");
        assertThat(dto.getAge()).isEqualTo(25);
        assertThat(dto.getTutorId()).isEqualTo(1L);
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    void toDTO_WithoutTutor_ShouldHaveNullTutorId() {
        User user = new User();
        user.setId(1L);
        user.setName("Luis");
        user.setEmail("luis@test.com");

        UserResponseDTO dto = mapper.toDTO(user);

        assertThat(dto.getTutorId()).isNull();
    }

    @Test
    void toEntity_ShouldIgnoreTutorId() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Pedro");
        dto.setSurname("Martínez");
        dto.setEmail("pedro@test.com");
        dto.setDni("87654321X");
        dto.setPassport("XYZ789012");
        dto.setAge(30);
        dto.setTutorId(5L);
        dto.setActive(true);

        User user = mapper.toEntity(dto);

        assertThat(user.getName()).isEqualTo("Pedro");
        assertThat(user.getSurname()).isEqualTo("Martínez");
        assertThat(user.getEmail()).isEqualTo("pedro@test.com");
        assertThat(user.getDni()).isEqualTo("87654321X");
        assertThat(user.getPassport()).isEqualTo("XYZ789012");
        assertThat(user.getAge()).isEqualTo(30);
        assertThat(user.getTutorId()).isNull();
        assertThat(user.getActive()).isTrue();
    }

    @Test
    void updateFromDto_ShouldIgnoreTutorId() {
        User tutor = new User();
        tutor.setId(1L);

        User user = new User();
        user.setName("Original");
        user.setTutorId(tutor);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Updated");
        dto.setTutorId(5L);

        mapper.updateFromDto(dto, user);

        assertThat(user.getName()).isEqualTo("Updated");
        assertThat(user.getTutorId()).isSameAs(tutor);
    }
}
