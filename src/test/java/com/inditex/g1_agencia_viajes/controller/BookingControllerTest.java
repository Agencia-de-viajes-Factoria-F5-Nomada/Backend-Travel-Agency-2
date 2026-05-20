package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.BookingQuoteRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingQuoteResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BookingRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BookingResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BookingUserRequestDTO;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJson
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Test
    void getAllBookings_ShouldReturn200() throws Exception {
        when(bookingService.findAll(any(Pageable.class), anyLong(), eq(Role.ADMIN)))
                .thenReturn(new PageImpl<>(List.of(new BookingResponseDTO())));

        mockMvc.perform(get("/api/bookings")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getBookingById_ShouldReturn200() throws Exception {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(1L);
        when(bookingService.findById(1L, 1L, Role.ADMIN)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/bookings/1")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1));
    }

    @Test
    void getBookingById_ShouldReturn404() throws Exception {
        when(bookingService.findById(99L, 1L, Role.ADMIN)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/bookings/99")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_ShouldReturn201() throws Exception {
        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(1L);
        when(bookingService.save(any(BookingRequestDTO.class), anyLong(), eq(Role.ADMIN))).thenReturn(response);

        String json = """
                {
                    "typeBoard": "HALF",
                    "travelId": 1,
                    "customerIds": [1, 2]
                }
                """;

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(1));
    }

    @Test
    void createBooking_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void quoteBooking_ShouldReturn200() throws Exception {
        when(bookingService.quote(any(BookingQuoteRequestDTO.class)))
                .thenReturn(new BookingQuoteResponseDTO());

        String json = """
                {
                    "travelId": 1,
                    "typeBoard": "HALF",
                    "passengers": [
                        {
                            "name": "Ana",
                            "surname": "Lopez",
                            "birthDate": "1995-01-01"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/bookings/quote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void updateBooking_ShouldReturn200() throws Exception {
        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingId(1L);
        when(bookingService.update(any(), any(BookingRequestDTO.class), anyLong(), eq(Role.ADMIN)))
                .thenReturn(response);

        String json = """
                {
                    "typeBoard": "FULL",
                    "travelId": 1,
                    "customerIds": [1]
                }
                """;

        mockMvc.perform(put("/api/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1));
    }

    @Test
    void addCustomerToBooking_ShouldReturn200() throws Exception {
        doNothing().when(bookingService).addCustomerToBooking(any(BookingUserRequestDTO.class), anyLong(), eq(Role.ADMIN));

        String json = """
                {
                    "userId": 2
                }
                """;

        mockMvc.perform(post("/api/bookings/1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void addCustomerToBooking_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = """
                {
                    "userId": null
                }
                """;

        mockMvc.perform(post("/api/bookings/1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBooking_ShouldReturn204() throws Exception {
        doNothing().when(bookingService).deleteById(1L, 1L, Role.ADMIN);

        mockMvc.perform(delete("/api/bookings/1")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isNoContent());
    }
}
