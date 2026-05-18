package com.inditex.g1_agencia_viajes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingUserRequestDTO {

    private Long bookingId;

    @NotNull(message = "El ID del cliente/usuario es obligatorio")
    private Long userId;

    public BookingUserRequestDTO() {}
}
