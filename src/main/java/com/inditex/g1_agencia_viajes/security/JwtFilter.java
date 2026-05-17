package com.inditex.g1_agencia_viajes.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.inditex.g1_agencia_viajes.model.Role;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter implements Filter {

    private final Algorithm algorithm;
 
     public JwtFilter(@Value("${jwt.secret}") String secret) {
         this.algorithm = Algorithm.HMAC256(secret);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        String method = req.getMethod();

        if (path.equals("/api/authentication/login")
                || path.startsWith("/api-docs")
                || path.startsWith("/swagger-ui")) {
            chain.doFilter(request, response);
            return;
        }

        if (method.equals("GET")
                && !path.startsWith("/api/users")
                && !path.startsWith("/api/bookings")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token ausente o inválido");
            return;
        }

        String token = authHeader.substring(7);

        try {
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("agencia-viajes").build();
            DecodedJWT jwt = verifier.verify(token);

            String roleStr = jwt.getClaim("role").asString();
            Role role = roleStr != null ? Role.valueOf(roleStr) : null;

            if (role == Role.VIEWER && !method.equals("GET")) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permisos para modificar datos");
                return;
            }

            if (role == Role.EDITOR && !method.equals("GET") && path.startsWith("/api/employees")) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permisos para gestionar empleados");
                return;
            }

            req.setAttribute("id", jwt.getClaim("id").asLong());
            req.setAttribute("role", role);

            chain.doFilter(request, response);
        } catch (JWTVerificationException exception) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
        }
    }
}
