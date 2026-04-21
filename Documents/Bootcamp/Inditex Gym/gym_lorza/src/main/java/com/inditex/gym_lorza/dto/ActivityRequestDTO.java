package com.inditex.gym_lorza.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
public class ActivityRequestDTO {

    @NotBlank
    private String title;
    @NotBlank
    private String price;
    @NotNull
    private Integer weekDay;
    @NotNull
    private LocalTime startHour;
    @NotNull
    private LocalTime endHour;
    private String image;
    private Long trainerId;
}