package com.inditex.g1_agencia_viajes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HotelRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    @Min(value = 1, message = "Las estrellas deben ser mínimo 1")
    @Max(value = 5, message = "Las estrellas deben ser máximo 5")
    private Integer stars;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que 0")
    private Integer capacity;

    @NotNull(message = "Las plazas disponibles son obligatorias")
    @Min(value = 0, message = "Las plazas no pueden ser negativas")
    private Integer availablePlaces;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double halfBoardPrice;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double fullBoardPrice;

    private String imageUrl;

    private Boolean active;
}