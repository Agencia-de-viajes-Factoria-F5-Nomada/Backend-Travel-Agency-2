package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.TripSegmentRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TripSegmentResponseDTO;
import com.inditex.g1_agencia_viajes.service.TripSegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip-segments")
@RequiredArgsConstructor
@Tag(name = "Trayectos", description = "Gestión de trayectos de viajes")
public class TripSegmentController {

    private final TripSegmentService tripSegmentService;

    @GetMapping
    @Operation(summary = "Obtener todos los trayectos")
    public ResponseEntity<Page<TripSegmentResponseDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(tripSegmentService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un trayecto por ID")
    public ResponseEntity<TripSegmentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tripSegmentService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo trayecto")
    public ResponseEntity<TripSegmentResponseDTO> create(@Valid @RequestBody TripSegmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripSegmentService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un trayecto existente")
    public ResponseEntity<TripSegmentResponseDTO> update(@PathVariable Long id,
                                                          @Valid @RequestBody TripSegmentRequestDTO dto) {
        return ResponseEntity.ok(tripSegmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un trayecto")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tripSegmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
