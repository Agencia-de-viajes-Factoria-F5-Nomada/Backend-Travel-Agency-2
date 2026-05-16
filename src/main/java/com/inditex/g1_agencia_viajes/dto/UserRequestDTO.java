package com.inditex.g1_agencia_viajes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String surname;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI no tiene un formato válido")
    private String dni;

    @Pattern(regexp = "^[A-Z]{3}[0-9]{6}$", message = "Formato de pasaporte inválido (ej: AAA123456)")
    private String passport;

    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer age;

    private Long tutorId;

    private Boolean active;
}