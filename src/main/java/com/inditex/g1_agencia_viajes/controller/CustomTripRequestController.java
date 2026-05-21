package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.CustomTripRequestDTO;
import com.inditex.g1_agencia_viajes.dto.CustomTripRequestResponseDTO;
import com.inditex.g1_agencia_viajes.service.CustomTripRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/custom-trip-requests")
@RequiredArgsConstructor
@Tag(name = "Solicitudes de Viaje Personalizado", description = "Gestión de solicitudes de viajes a medida")
public class CustomTripRequestController {

    private final CustomTripRequestService customTripRequestService;

    @PostMapping
    @Operation(summary = "Crear una nueva solicitud de viaje personalizado")
    public ResponseEntity<CustomTripRequestResponseDTO> createCustomTripRequest(
            @Valid @RequestBody CustomTripRequestDTO dto,
            @RequestAttribute("id") Long currentUserId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customTripRequestService.save(dto, currentUserId));
    }
}