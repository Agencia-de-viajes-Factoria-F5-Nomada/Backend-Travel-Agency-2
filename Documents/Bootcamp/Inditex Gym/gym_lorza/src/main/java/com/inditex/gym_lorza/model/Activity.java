package com.inditex.gym_lorza.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne
    @JoinColumn(name = "trainers_id")
    @JsonIgnoreProperties("activities")
    private Trainer trainer;
}