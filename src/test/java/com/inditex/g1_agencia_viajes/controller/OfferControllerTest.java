package com.inditex.g1_agencia_viajes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.g1_agencia_viajes.dto.OfferRequestDTO;
import com.inditex.g1_agencia_viajes.dto.OfferResponseDTO;
import com.inditex.g1_agencia_viajes.exception.GlobalExceptionHandler;
import com.inditex.g1_agencia_viajes.service.OfferService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferController offerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(offerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_ShouldReturn200() throws Exception {
        when(offerService.findAll()).thenReturn(List.of(new OfferResponseDTO()));

        mockMvc.perform(get("/api/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        OfferResponseDTO dto = new OfferResponseDTO();
        dto.setOfferId(1L);
        dto.setDiscountPercentage(10.0);
        when(offerService.findById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/offers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(1))
                .andExpect(jsonPath("$.discountPercentage").value(10.0));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(offerService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/offers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        OfferResponseDTO response = new OfferResponseDTO();
        response.setOfferId(1L);
        response.setDiscountPercentage(15.0);
        when(offerService.save(any(OfferRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "discountPercentage": 15.0,
                    "startDate": "2026-06-01",
                    "endDate": "2026-06-30"
                }
                """;

        mockMvc.perform(post("/api/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.discountPercentage").value(15.0));
    }

    @Test
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        OfferResponseDTO response = new OfferResponseDTO();
        response.setOfferId(1L);
        response.setDiscountPercentage(20.0);
        when(offerService.update(any(), any(OfferRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "discountPercentage": 20.0,
                    "startDate": "2026-07-01",
                    "endDate": "2026-07-31"
                }
                """;

        mockMvc.perform(put("/api/offers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discountPercentage").value(20.0));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(offerService).deleteById(1L);

        mockMvc.perform(delete("/api/offers/1"))
                .andExpect(status().isNoContent());
    }
}
