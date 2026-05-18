package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.LoginRequest;
import com.inditex.g1_agencia_viajes.dto.LoginResponse;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import com.inditex.g1_agencia_viajes.security.JwtUtil;
import com.inditex.g1_agencia_viajes.security.LoginRateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Inicio de sesión de empleados")
public class AuthenticationController {

    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final LoginRateLimiter rateLimiter;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un empleado y devuelve un token JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        if (rateLimiter.isBlocked(clientIp)) {
            long remainingSeconds = rateLimiter.getRemainingBlockSeconds(clientIp);
            long remainingMinutes = remainingSeconds / 60;
            return ResponseEntity.status(429)
                    .body("Demasiados intentos. Intente de nuevo en " + remainingMinutes + " minutos");
        }

        Employee employee = employeeRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (employee != null && BCrypt.checkpw(loginRequest.getPassword(), employee.getPassword())) {
            rateLimiter.resetAttempts(clientIp);
            String token = jwtUtil.createToken(employee.getEmail(), employee.getEmployeeId(), employee.getRole());
            return ResponseEntity.ok(new LoginResponse(token, employee.getEmployeeId(), employee.getName(), employee.getSurname(), employee.getRole()));
        }

        rateLimiter.recordFailedAttempt(clientIp);
        return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
