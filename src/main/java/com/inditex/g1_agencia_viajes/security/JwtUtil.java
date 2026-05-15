package com.inditex.g1_agencia_viajes.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.inditex.g1_agencia_viajes.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final Algorithm algoritmo;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationMs) {
        this.algoritmo = Algorithm.HMAC256(secret);
        this.expirationMs = expirationMs;
    }

    public String crearToken(String email, Long id, Role role) {
        return JWT.create()
                .withSubject(email)
                .withClaim("id", id)
                .withClaim("role", role.name())
                .withIssuer("agencia-viajes")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .sign(algoritmo);
    }

    public Algorithm getAlgoritmo() {
        return algoritmo;
    }
}
