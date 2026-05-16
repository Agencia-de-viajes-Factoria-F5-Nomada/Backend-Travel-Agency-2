package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.service.BusService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJson
class BusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BusService busService;



    @Test
    void getAll_ShouldReturn200() throws Exception {
        when(busService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new BusResponseDTO())));

        mockMvc.perform(get("/api/buses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        BusResponseDTO dto = new BusResponseDTO();
        dto.setId(1L);
        dto.setLicensePlate("1234-ABC");
        when(busService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/buses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("1234-ABC"));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(busService.getById(99L)).thenThrow(new ResourceNotFoundException("el autobús", 99L));

        mockMvc.perform(get("/api/buses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        BusResponseDTO response = new BusResponseDTO();
        response.setId(1L);
        response.setLicensePlate("9999-ZZZ");
        when(busService.create(any(BusRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "licensePlate": "9999-ZZZ",
                    "capacity": 50
                }
                """;

        mockMvc.perform(post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("9999-ZZZ"));
    }

    @Test
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        BusResponseDTO response = new BusResponseDTO();
        response.setId(1L);
        response.setLicensePlate("8888-YYY");
        when(busService.update(any(), any(BusRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "licensePlate": "8888-YYY",
                    "capacity": 40
                }
                """;

        mockMvc.perform(put("/api/buses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("8888-YYY"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(busService).delete(1L);

        mockMvc.perform(delete("/api/buses/1"))
                .andExpect(status().isNoContent());
    }
}
