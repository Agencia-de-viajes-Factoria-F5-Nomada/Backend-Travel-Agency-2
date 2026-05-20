package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidFileExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new InvalidFileException("Formato de archivo no soportado");
        assertThat(exception.getMessage()).isEqualTo("Formato de archivo no soportado");
    }
}
