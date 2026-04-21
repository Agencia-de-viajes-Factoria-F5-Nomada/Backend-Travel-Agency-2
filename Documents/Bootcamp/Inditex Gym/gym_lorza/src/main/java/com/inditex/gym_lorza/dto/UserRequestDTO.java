package com.inditex.gym_lorza.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String dni;
    @NotNull
    private Integer startYear;
    @NotNull
    private Boolean isActive;
    private String image;
}