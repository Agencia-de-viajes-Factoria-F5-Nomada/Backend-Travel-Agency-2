package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TravelNotAvailableExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new TravelNotAvailableException(7L);
        assertThat(exception.getMessage())
                .isEqualTo("El viaje con id: 7 no tiene plazas disponibles");
    }
}
