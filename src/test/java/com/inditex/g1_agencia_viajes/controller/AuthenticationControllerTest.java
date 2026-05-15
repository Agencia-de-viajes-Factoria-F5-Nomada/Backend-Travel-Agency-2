package com.inditex.g1_agencia_viajes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.g1_agencia_viajes.dto.LoginRequest;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import com.inditex.g1_agencia_viajes.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void login_WithValidCredentials_ShouldReturn200() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Carlos");
        employee.setSurname("Pérez");
        employee.setEmail("carlos@nomada.es");
        employee.setRole(Role.ADMIN);
        employee.setPassword("$2a$10$8X.MsojZ2enH58GWd9AdlOMWs7XuPzcixfUccJL.3zCTizOsggiTO");

        when(employeeRepository.findByEmail("carlos@nomada.es")).thenReturn(Optional.of(employee));
        when(jwtUtil.crearToken(any(), any(), any())).thenReturn("fake-jwt-token");

        String json = """
                {
                    "email": "carlos@nomada.es",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void login_WithInvalidEmail_ShouldReturn401() throws Exception {
        when(employeeRepository.findByEmail("unknown@nomada.es")).thenReturn(Optional.empty());

        String json = """
                {
                    "email": "unknown@nomada.es",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithInvalidPassword_ShouldReturn401() throws Exception {
        Employee employee = new Employee();
        employee.setEmail("carlos@nomada.es");
        employee.setPassword("$2a$10$8X.MsojZ2enH58GWd9AdlOMWs7XuPzcixfUccJL.3zCTizOsggiTO");

        when(employeeRepository.findByEmail("carlos@nomada.es")).thenReturn(Optional.of(employee));

        String json = """
                {
                    "email": "carlos@nomada.es",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithInvalidBody_ShouldReturn400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
