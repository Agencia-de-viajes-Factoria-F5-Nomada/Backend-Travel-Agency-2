package com.inditex.gym_lorza.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String name;
    private String surname;
    private String dni;
    private Integer startYear;
    private Boolean isActive;
    private Boolean annualFeePaid;
    private String image;
}