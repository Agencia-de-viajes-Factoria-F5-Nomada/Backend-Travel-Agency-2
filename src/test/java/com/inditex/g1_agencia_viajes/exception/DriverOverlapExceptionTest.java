package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DriverOverlapExceptionTest {

    @Test
    void exception_ShouldHaveCorrectMessage() {
        var exception = new DriverOverlapException(1L, "2026-06-01T10:00", "2026-06-01T14:00");
        assertThat(exception.getMessage())
                .isEqualTo("El conductor con id: 1 ya tiene un trayecto entre 2026-06-01T10:00 y 2026-06-01T14:00");
    }
}
