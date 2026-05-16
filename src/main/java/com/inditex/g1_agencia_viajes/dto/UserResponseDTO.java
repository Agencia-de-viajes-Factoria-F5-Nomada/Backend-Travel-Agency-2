package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String dni;
    private String passport;
    private Integer age;
    private Long tutorId;
    private Boolean active;
}