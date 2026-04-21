package com.inditex.gym_lorza.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ActivityResponseDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private LocalDate date;
    private LocalTime startHour;
    private LocalTime endHour;
    private String image;
    private String trainerName;
}