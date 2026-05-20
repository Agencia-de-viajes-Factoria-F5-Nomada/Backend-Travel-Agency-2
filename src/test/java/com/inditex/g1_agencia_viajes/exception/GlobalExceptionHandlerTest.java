package com.inditex.g1_agencia_viajes.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFound_ShouldReturn404() {
        var exception = new ResourceNotFoundException("el test", 1L);
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleTravelNotAvailable_ShouldReturn409() {
        var exception = new TravelNotAvailableException(1L);
        ResponseEntity<Map<String, String>> response = handler.handleTravelNotAvailable(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleHotelNotAvailable_ShouldReturn409() {
        var exception = new HotelNotAvailableException(1L);
        ResponseEntity<Map<String, String>> response = handler.handleHotelNotAvailable(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleEmailAlreadyExists_ShouldReturn409() {
        var exception = new EmailAlreadyExistsException("test@test.com");
        ResponseEntity<Map<String, String>> response = handler.handleEmailAlreadyExists(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleMinorWithoutTutor_ShouldReturn400() {
        var exception = new MinorWithoutTutorException();
        ResponseEntity<Map<String, String>> response = handler.handleMinorWithoutTutor(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handlePassportRequired_ShouldReturn400() {
        var exception = new PassportRequiredException("test");
        ResponseEntity<Map<String, String>> response = handler.handlePassportRequired(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleBusFull_ShouldReturn409() {
        var exception = new BusFullException(1L, "1234-ABC");
        ResponseEntity<Map<String, String>> response = handler.handleBusFull(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleDriverOverlap_ShouldReturn409() {
        var exception = new DriverOverlapException(1L, "10:00", "12:00");
        ResponseEntity<Map<String, String>> response = handler.handleDriverOverlap(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handlePastTravel_ShouldReturn409() {
        var exception = new PastTravelException(1L);
        ResponseEntity<Map<String, String>> response = handler.handlePastTravel(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleDuplicateLicensePlate_ShouldReturn409() {
        var exception = new DuplicateLicensePlateException("1234-ABC");
        ResponseEntity<Map<String, String>> response = handler.handleDuplicateLicensePlate(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleInvalidFile_ShouldReturn400() {
        var exception = new InvalidFileException("bad file");
        ResponseEntity<Map<String, String>> response = handler.handleInvalidFile(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleIllegalArgument_ShouldReturn400() {
        var exception = new IllegalArgumentException("bad arg");
        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", exception.getMessage());
    }

    @Test
    void handleAllUncaught_ShouldReturn500() {
        var exception = new RuntimeException("unexpected");
        ResponseEntity<Map<String, String>> response = handler.handleAllUncaught(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", "Ha ocurrido un error inesperado");
    }
}
