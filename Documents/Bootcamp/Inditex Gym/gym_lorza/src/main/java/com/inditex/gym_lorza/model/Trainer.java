package com.inditex.gym_lorza.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="trainers")
@Getter
@Setter
@NoArgsConstructor
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private  String dni;
    @NotNull
    private  Integer hiringYear;
    @NotNull
    private  Boolean isHired;

    private String image;

    @OneToMany(mappedBy = "trainer")
    @JsonIgnoreProperties("trainer")
    private List<Activity> activities = new ArrayList<>();
}
