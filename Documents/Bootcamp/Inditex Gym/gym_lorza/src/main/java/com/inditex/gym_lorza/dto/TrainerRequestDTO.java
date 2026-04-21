package com.inditex.gym_lorza.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerRequestDTO {

    @NotBlank
    private String name;
    @NotBlank
    private String dni;
    @NotNull
    private Integer hiringYear;
    @NotNull
    private Boolean isHired;
    private String image;
}