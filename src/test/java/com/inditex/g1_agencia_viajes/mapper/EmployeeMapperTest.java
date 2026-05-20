package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.EmployeeRequestDTO;
import com.inditex.g1_agencia_viajes.dto.EmployeeResponseDTO;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);

    @Test
    void toEntity_ShouldMapAllFieldsExceptPassword() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("Carlos");
        dto.setSurname("Ruiz");
        dto.setEmail("carlos@nomada.es");
        dto.setPassword("secret");
        dto.setRole(Role.ADMIN);

        Employee employee = mapper.toEntity(dto);

        assertThat(employee.getName()).isEqualTo("Carlos");
        assertThat(employee.getSurname()).isEqualTo("Ruiz");
        assertThat(employee.getEmail()).isEqualTo("carlos@nomada.es");
        assertThat(employee.getPassword()).isNull();
        assertThat(employee.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void toDTO_ShouldMapAllFields() {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Ana");
        employee.setSurname("López");
        employee.setEmail("ana@nomada.es");
        employee.setPassword("hashed");
        employee.setRole(Role.EMPLOYEE);
        employee.setActive(true);

        EmployeeResponseDTO dto = mapper.toDTO(employee);

        assertThat(dto.getEmployeeId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Ana");
        assertThat(dto.getSurname()).isEqualTo("López");
        assertThat(dto.getEmail()).isEqualTo("ana@nomada.es");
        assertThat(dto.getRole()).isEqualTo(Role.EMPLOYEE);
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    void updateFromDto_ShouldNotUpdatePassword() {
        Employee employee = new Employee();
        employee.setPassword("original");

        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("Updated");
        dto.setPassword("newpass");

        mapper.updateFromDto(dto, employee);

        assertThat(employee.getName()).isEqualTo("Updated");
        assertThat(employee.getPassword()).isEqualTo("original");
    }
}
