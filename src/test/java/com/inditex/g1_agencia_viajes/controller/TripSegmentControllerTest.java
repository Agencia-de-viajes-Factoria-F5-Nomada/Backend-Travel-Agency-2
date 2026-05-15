package com.inditex.g1_agencia_viajes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.g1_agencia_viajes.dto.TripSegmentRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TripSegmentResponseDTO;
import com.inditex.g1_agencia_viajes.exception.GlobalExceptionHandler;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.service.TripSegmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TripSegmentControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TripSegmentService tripSegmentService;

    @InjectMocks
    private TripSegmentController tripSegmentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripSegmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_ShouldReturn200() throws Exception {
        when(tripSegmentService.getAll()).thenReturn(List.of(new TripSegmentResponseDTO()));

        mockMvc.perform(get("/api/trip-segments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        TripSegmentResponseDTO dto = new TripSegmentResponseDTO();
        dto.setId(1L);
        dto.setOrigin("Madrid");
        when(tripSegmentService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/trip-segments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("Madrid"));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(tripSegmentService.getById(99L)).thenThrow(new ResourceNotFoundException("el trayecto", 99L));

        mockMvc.perform(get("/api/trip-segments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        TripSegmentResponseDTO response = new TripSegmentResponseDTO();
        response.setId(1L);
        response.setOrigin("Barcelona");
        when(tripSegmentService.create(any(TripSegmentRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "travelId": 1,
                    "origin": "Barcelona",
                    "destination": "Madrid",
                    "startTime": "2026-08-01T10:00:00",
                    "endTime": "2026-08-01T14:00:00",
                    "busId": 1,
                    "driverId": 1
                }
                """;

        mockMvc.perform(post("/api/trip-segments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin").value("Barcelona"));
    }

    @Test
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/trip-segments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        TripSegmentResponseDTO response = new TripSegmentResponseDTO();
        response.setId(1L);
        response.setOrigin("Valencia");
        when(tripSegmentService.update(any(), any(TripSegmentRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "travelId": 1,
                    "origin": "Valencia",
                    "destination": "Sevilla",
                    "startTime": "2026-09-01T10:00:00",
                    "endTime": "2026-09-01T14:00:00",
                    "busId": 1,
                    "driverId": 1
                }
                """;

        mockMvc.perform(put("/api/trip-segments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("Valencia"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(tripSegmentService).delete(1L);

        mockMvc.perform(delete("/api/trip-segments/1"))
                .andExpect(status().isNoContent());
    }
}
