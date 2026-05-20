package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.UserRequestDTO;
import com.inditex.g1_agencia_viajes.dto.UserResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJson
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;



    @Test
    void getAll_ShouldReturn200() throws Exception {
        when(userService.getAll(any(Pageable.class), anyLong(), eq(Role.ADMIN)))
                .thenReturn(new PageImpl<>(List.of(new UserResponseDTO())));

        mockMvc.perform(get("/api/users")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(1L);
        dto.setName("John");

        when(userService.getById(1L, 1L, Role.ADMIN)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(userService.getById(99L, 1L, Role.ADMIN))
                .thenThrow(new ResourceNotFoundException("el usuario", 99L));

        mockMvc.perform(get("/api/users/99")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActive_ShouldReturn200() throws Exception {
        when(userService.getActive(any(Pageable.class), anyLong(), eq(Role.ADMIN)))
                .thenReturn(new PageImpl<>(List.of(new UserResponseDTO())));

        mockMvc.perform(get("/api/users/active")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("John");
        when(userService.create(any(UserRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "name": "John",
                    "surname": "Doe",
                    "email": "john@email.com",
                    "dni": "12345678A",
                    "age": 30
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("John Updated");
        when(userService.update(any(), any(UserRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "name": "John Updated",
                    "surname": "Doe",
                    "email": "john@email.com",
                    "dni": "12345678A"
                }
                """;

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
