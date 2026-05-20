package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinorWithoutTutorExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new MinorWithoutTutorException();
        assertThat(exception.getMessage())
                .isEqualTo("Un menor de edad debe ir acompañado de un tutor para crear la reserva");
    }
}
