package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateLicensePlateExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new DuplicateLicensePlateException("1234-ABC");
        assertThat(exception.getMessage())
                .isEqualTo("Ya existe un autobús con la matrícula: 1234-ABC");
    }
}
