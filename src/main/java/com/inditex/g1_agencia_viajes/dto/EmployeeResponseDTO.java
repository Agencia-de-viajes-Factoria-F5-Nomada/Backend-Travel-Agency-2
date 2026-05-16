package com.inditex.g1_agencia_viajes.dto;

import com.inditex.g1_agencia_viajes.model.Gender;
import com.inditex.g1_agencia_viajes.model.Role;
import lombok.Data;

@Data
public class EmployeeResponseDTO {
    private Long employeeId;
    private String name;
    private String surname;
    private String email;
    private Gender gender;
    private Integer workHour;
    private Boolean hired;
    private Role role;
}
