package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new ResourceNotFoundException("el cliente", 42L);
        assertThat(exception.getMessage())
                .isEqualTo("No hemos podido encontrar la información de el cliente, con el id: 42");
    }
}
