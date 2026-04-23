package com.inditex.gym_lorza.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @NotNull
    private Boolean annualFeePaid;

    private String image;

    @ManyToMany(mappedBy = "users")
    @JsonIgnoreProperties("users")
    private Set<Activity> activities = new HashSet<>();
}