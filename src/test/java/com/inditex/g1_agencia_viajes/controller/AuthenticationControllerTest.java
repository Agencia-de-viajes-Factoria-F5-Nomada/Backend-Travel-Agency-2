package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.LoginRequest;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import com.inditex.g1_agencia_viajes.security.JwtUtil;
import com.inditex.g1_agencia_viajes.security.LoginRateLimiter;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJson
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private LoginRateLimiter rateLimiter;



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
        when(jwtUtil.createToken(any(), any(), any())).thenReturn("fake-jwt-token");

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
