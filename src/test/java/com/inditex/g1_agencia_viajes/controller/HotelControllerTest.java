package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.HotelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.HotelResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.service.HotelService;
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

@WebMvcTest(HotelController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJson
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HotelService hotelService;



    @Test
    void getAll_ShouldReturn200() throws Exception {
        when(hotelService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new HotelResponseDTO())));

        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        HotelResponseDTO dto = new HotelResponseDTO();
        dto.setId(1L);
        dto.setName("Hotel Test");
        when(hotelService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hotel Test"));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(hotelService.getById(99L)).thenThrow(new ResourceNotFoundException("el hotel", 99L));

        mockMvc.perform(get("/api/hotels/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActive_ShouldReturn200() throws Exception {
        when(hotelService.getActive(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new HotelResponseDTO())));

        mockMvc.perform(get("/api/hotels/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAvailable_ShouldReturn200() throws Exception {
        when(hotelService.getAvailable(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new HotelResponseDTO())));

        mockMvc.perform(get("/api/hotels/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        HotelResponseDTO response = new HotelResponseDTO();
        response.setId(1L);
        response.setName("Nuevo Hotel");
        when(hotelService.create(any(HotelRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "name": "Nuevo Hotel",
                    "address": "Calle 123",
                    "city": "Madrid",
                    "country": "España",
                    "stars": 4,
                    "capacity": 100,
                    "availablePlaces": 100,
                    "halfBoardPrice": 80.0,
                    "fullBoardPrice": 120.0
                }
                """;

        mockMvc.perform(post("/api/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nuevo Hotel"));
    }

    @Test
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        HotelResponseDTO response = new HotelResponseDTO();
        response.setId(1L);
        response.setName("Hotel Actualizado");
        when(hotelService.update(any(), any(HotelRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "name": "Hotel Actualizado",
                    "address": "Calle 456",
                    "city": "Barcelona",
                    "country": "España",
                    "stars": 5,
                    "capacity": 100,
                    "availablePlaces": 90
                }
                """;

        mockMvc.perform(put("/api/hotels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hotel Actualizado"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(hotelService).delete(1L);

        mockMvc.perform(delete("/api/hotels/1"))
                .andExpect(status().isNoContent());
    }
}
