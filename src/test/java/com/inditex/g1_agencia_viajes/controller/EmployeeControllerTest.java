package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.EmployeeRequestDTO;
import com.inditex.g1_agencia_viajes.dto.EmployeeResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Gender;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.service.EmployeeService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJson
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;



    @Test
    void getAll_ShouldReturn200() throws Exception {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(1L);
        dto.setName("Carlos");
        when(employeeService.getAllEmployees(any(Pageable.class), anyLong(), eq(Role.ADMIN)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/employees")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(1L);
        dto.setName("Carlos");
        dto.setEmail("carlos@nomada.es");
        when(employeeService.getEmployeeById(1L, 1L, Role.ADMIN)).thenReturn(dto);

        mockMvc.perform(get("/api/employees/1")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos"));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(employeeService.getEmployeeById(99L, 1L, Role.ADMIN))
                .thenThrow(new ResourceNotFoundException("el empleado", 99L));

        mockMvc.perform(get("/api/employees/99")
                        .requestAttr("id", 1L)
                        .requestAttr("role", Role.ADMIN))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setEmployeeId(1L);
        response.setName("Nuevo");
        response.setEmail("nuevo@nomada.es");
        when(employeeService.saveEmployee(any(EmployeeRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "name": "Nuevo",
                    "surname": "Empleado",
                    "email": "nuevo@nomada.es",
                    "gender": "MALE",
                    "hired": true,
                    "role": "EMPLOYEE",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nuevo"));
    }

    @Test
    void create_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = """
                {
                    "name": "",
                    "surname": "",
                    "email": "invalido",
                    "gender": "",
                    "hired": true,
                    "role": "",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setEmployeeId(1L);
        response.setName("Actualizado");
        response.setEmail("actualizado@nomada.es");
        when(employeeService.updateEmployee(any(), any(EmployeeRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "name": "Actualizado",
                    "surname": "Empleado",
                    "email": "actualizado@nomada.es",
                    "gender": "MALE",
                    "hired": true,
                    "role": "ADMIN",
                    "password": "123456"
                }
                """;

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Actualizado"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("el empleado", 99L))
                .when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());
    }
}
