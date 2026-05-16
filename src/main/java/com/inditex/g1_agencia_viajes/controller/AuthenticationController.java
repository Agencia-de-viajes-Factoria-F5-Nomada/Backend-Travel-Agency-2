package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.LoginRequest;
import com.inditex.g1_agencia_viajes.dto.LoginResponse;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import com.inditex.g1_agencia_viajes.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
@Tag(name = "Autenticación", description = "Inicio de sesión de empleados")
public class AuthenticationController {

    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationController(EmployeeRepository employeeRepository, JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un empleado y devuelve un token JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Employee employee = employeeRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (employee != null && BCrypt.checkpw(loginRequest.getPassword(), employee.getPassword())) {
            String token = jwtUtil.crearToken(employee.getEmail(), employee.getEmployeeId(), employee.getRole());
            return ResponseEntity.ok(new LoginResponse(token, employee.getEmployeeId(), employee.getName(), employee.getSurname(), employee.getRole()));
        }

        return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
    }
}
