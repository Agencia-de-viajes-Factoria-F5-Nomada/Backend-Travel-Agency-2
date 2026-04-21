package com.inditex.gym_lorza.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerResponseDTO {

    private Long id;
    private String name;
    private String dni;
    private Integer hiringYear;
    private Boolean isHired;
    private String image;
}