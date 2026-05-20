package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusFullExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new BusFullException(1L, "1234-ABC");
        assertThat(exception.getMessage())
                .isEqualTo("El autobús con matrícula 1234-ABC (id: 1) no tiene suficientes plazas disponibles");
    }
}
