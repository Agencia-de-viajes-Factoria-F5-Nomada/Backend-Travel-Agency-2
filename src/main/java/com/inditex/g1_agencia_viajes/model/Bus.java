package com.inditex.g1_agencia_viajes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La matrícula es obligatoria")
    @Column(nullable = false, unique = true)
    private String licensePlate;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que 0")
    @Column(nullable = false)
    private Integer capacity;

    @Min(value = 0, message = "Las plazas no pueden ser negativas")
    private Integer availablePlaces;

    private Boolean bath;
    private Boolean wifi;
    private Boolean AC;
    private Boolean USB;
    private Boolean active = true;
}