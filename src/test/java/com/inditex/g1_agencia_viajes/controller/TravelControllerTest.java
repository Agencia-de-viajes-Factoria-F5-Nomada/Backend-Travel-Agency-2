package com.inditex.g1_agencia_viajes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.g1_agencia_viajes.dto.TravelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TravelResponseDTO;
import com.inditex.g1_agencia_viajes.exception.GlobalExceptionHandler;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.service.TravelService;
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
class TravelControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TravelService travelService;

    @InjectMocks
    private TravelController travelController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(travelController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_ShouldReturn200() throws Exception {
        when(travelService.getAll()).thenReturn(List.of(new TravelResponseDTO()));

        mockMvc.perform(get("/api/travels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAvailable_ShouldReturn200() throws Exception {
        when(travelService.getAvailable()).thenReturn(List.of(new TravelResponseDTO()));

        mockMvc.perform(get("/api/travels/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getOnSale_ShouldReturn200() throws Exception {
        TravelResponseDTO dto = new TravelResponseDTO();
        dto.setSale(true);
        when(travelService.getOnSale()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/travels/sale"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        TravelResponseDTO dto = new TravelResponseDTO();
        dto.setId(1L);
        dto.setDestiny("Londres");
        when(travelService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/travels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.destiny").value("Londres"));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(travelService.getById(99L)).thenThrow(new ResourceNotFoundException("el viaje", 99L));

        mockMvc.perform(get("/api/travels/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        TravelResponseDTO response = new TravelResponseDTO();
        response.setId(1L);
        response.setDestiny("París");
        when(travelService.create(any(TravelRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "destiny": "París",
                    "startDate": "2026-08-01",
                    "endDate": "2026-08-05",
                    "availablePlaces": 30,
                    "hotelId": 1
                }
                """;

        mockMvc.perform(post("/api/travels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destiny").value("París"));
    }

    @Test
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/travels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        TravelResponseDTO response = new TravelResponseDTO();
        response.setId(1L);
        response.setDestiny("Roma");
        when(travelService.update(any(), any(TravelRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "destiny": "Roma",
                    "startDate": "2026-09-01",
                    "endDate": "2026-09-05",
                    "availablePlaces": 25,
                    "hotelId": 1
                }
                """;

        mockMvc.perform(put("/api/travels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destiny").value("Roma"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(travelService).delete(1L);

        mockMvc.perform(delete("/api/travels/1"))
                .andExpect(status().isNoContent());
    }
}
