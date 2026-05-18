package com.inditex.g1_agencia_viajes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    @Min(value = 1, message = "Las estrellas deben ser mínimo 1")
    private Integer stars;

    private Integer capacity;

    private Integer availablePlaces;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double halfBoardPrice;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double fullBoardPrice;

    private String imageUrl;

    private Boolean active = true;
}