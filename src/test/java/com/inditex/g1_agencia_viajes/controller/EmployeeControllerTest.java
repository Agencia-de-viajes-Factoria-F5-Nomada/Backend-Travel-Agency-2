package com.inditex.g1_agencia_viajes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.g1_agencia_viajes.exception.GlobalExceptionHandler;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.service.EmployeeService;
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
class EmployeeControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_ShouldReturn200() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Carlos");
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Carlos");
        employee.setEmail("carlos@nomada.es");
        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos"));
    }

    @Test
    void getById_ShouldReturn404() throws Exception {
        when(employeeService.getEmployeeById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Nuevo");
        employee.setEmail("nuevo@nomada.es");
        when(employeeService.saveEmployee(any(Employee.class))).thenReturn(employee);

        String json = """
                {
                    "name": "Nuevo",
                    "surname": "Empleado",
                    "email": "nuevo@nomada.es",
                    "gender": "MALE",
                    "hired": true,
                    "role": "VIEWER",
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
        when(employeeService.saveEmployee(any(Employee.class)))
                .thenThrow(new IllegalArgumentException("El email debe ser del dominio @nomada.es"));

        String json = "{}";

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Actualizado");
        employee.setEmail("actualizado@nomada.es");
        when(employeeService.updateEmployee(any(), any(Employee.class))).thenReturn(employee);

        String json = """
                {
                    "name": "Actualizado",
                    "surname": "Empleado",
                    "email": "actualizado@nomada.es",
                    "gender": "MALE",
                    "hired": true,
                    "role": "EDITOR"
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
        when(employeeService.getEmployeeById(1L)).thenReturn(new Employee());
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenNotFound() throws Exception {
        when(employeeService.getEmployeeById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());
    }
}
