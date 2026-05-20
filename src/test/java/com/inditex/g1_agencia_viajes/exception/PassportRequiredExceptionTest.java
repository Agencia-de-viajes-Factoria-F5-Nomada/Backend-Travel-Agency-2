package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PassportRequiredExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new PassportRequiredException("El pasaporte es obligatorio");
        assertThat(exception.getMessage()).isEqualTo("El pasaporte es obligatorio");
    }
}
