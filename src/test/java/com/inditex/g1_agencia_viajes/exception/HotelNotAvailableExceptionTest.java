package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HotelNotAvailableExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new HotelNotAvailableException(5L);
        assertThat(exception.getMessage())
                .isEqualTo("Hotel con id: 5 no tiene plazas disponibles");
    }
}
