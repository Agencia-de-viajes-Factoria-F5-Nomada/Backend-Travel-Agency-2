package com.inditex.g1_agencia_viajes.dto;

import com.inditex.g1_agencia_viajes.model.Gender;
import com.inditex.g1_agencia_viajes.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String surname;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotNull(message = "El género es obligatorio")
    private Gender gender;

    private Integer workHour;

    @NotNull(message = "El estado de contratación es obligatorio")
    private Boolean hired;

    @NotNull(message = "El rol es obligatorio")
    private Role role;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
