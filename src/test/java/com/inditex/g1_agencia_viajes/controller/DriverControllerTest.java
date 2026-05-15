package com.inditex.g1_agencia_viajes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.g1_agencia_viajes.dto.DriverRequestDTO;
import com.inditex.g1_agencia_viajes.dto.DriverResponseDTO;
import com.inditex.g1_agencia_viajes.exception.GlobalExceptionHandler;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.service.DriverService;
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
class DriverControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DriverService driverService;

    @InjectMocks
    private DriverController driverController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(driverController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_ShouldReturn200() throws Exception {
        when(driverService.getAll()).thenReturn(List.of(new DriverResponseDTO()));

        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        DriverResponseDTO dto = new DriverResponseDTO();
        dto.setId(1L);
        dto.setName("John Smith");
        when(driverService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/drivers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Smith"));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(driverService.getById(99L)).thenThrow(new ResourceNotFoundException("el conductor", 99L));

        mockMvc.perform(get("/api/drivers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActive_ShouldReturn200() throws Exception {
        when(driverService.getActive()).thenReturn(List.of(new DriverResponseDTO()));

        mockMvc.perform(get("/api/drivers/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        DriverResponseDTO response = new DriverResponseDTO();
        response.setId(1L);
        response.setName("New Driver");
        when(driverService.create(any(DriverRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "name": "New Driver",
                    "phone": "123456789"
                }
                """;

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Driver"));
    }

    @Test
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        DriverResponseDTO response = new DriverResponseDTO();
        response.setId(1L);
        response.setName("Updated Driver");
        when(driverService.update(any(), any(DriverRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "name": "Updated Driver",
                    "phone": "987654321"
                }
                """;

        mockMvc.perform(put("/api/drivers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Driver"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(driverService).delete(1L);

        mockMvc.perform(delete("/api/drivers/1"))
                .andExpect(status().isNoContent());
    }
}
