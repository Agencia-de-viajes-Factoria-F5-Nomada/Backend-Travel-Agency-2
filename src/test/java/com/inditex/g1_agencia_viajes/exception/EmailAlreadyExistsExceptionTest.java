package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAlreadyExistsExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new EmailAlreadyExistsException("test@example.com");
        assertThat(exception.getMessage())
                .isEqualTo("Ya existe un usuario con el email: test@example.com");
    }
}
