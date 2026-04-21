package com.inditex.gym_lorza.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

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
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime startHour;

    @NotNull
    private LocalTime endHour;

    private String image;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    @JsonIgnoreProperties("activities")
    private Trainer trainer;

    @ManyToMany
    @JoinTable(
            name = "activity_users",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties("activities")
    private Set<User> users = new HashSet<>();
}