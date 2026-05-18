package com.inditex.g1_agencia_viajes.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TravelRequestDTO {

    @NotBlank(message = "El destino es obligatorio")
    private String destiny;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate endDate;

    private Boolean sale = false;

    @NotNull(message = "Las plazas son obligatorias")
    @Min(value = 1, message = "Debe haber al menos una plaza")
    private Integer availablePlaces;

    @NotNull(message = "El hotel es obligatorio")
    private Long hotelId;

    @AssertTrue(message = "La fecha de fin debe ser posterior a la de inicio")
    @JsonIgnore
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) return true;
        return endDate.isAfter(startDate);
    }
}