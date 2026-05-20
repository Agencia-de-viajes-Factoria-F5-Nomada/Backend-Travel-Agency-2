package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PastTravelExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new PastTravelException(10L);
        assertThat(exception.getMessage())
                .isEqualTo("El viaje con el id: 10, ya ha terminado");
    }
}
