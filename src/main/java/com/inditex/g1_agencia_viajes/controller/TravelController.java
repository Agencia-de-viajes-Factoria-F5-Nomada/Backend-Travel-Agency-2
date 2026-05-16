package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.TravelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TravelResponseDTO;
import com.inditex.g1_agencia_viajes.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travels")
@Tag(name = "Viajes", description = "Gestión de viajes")
public class TravelController {

    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los viajes")
    public ResponseEntity<List<TravelResponseDTO>> getAll() {
        return ResponseEntity.ok(travelService.getAll());
    }

    @GetMapping("/available")
    @Operation(summary = "Obtener viajes disponibles", description = "Viajes futuros con plazas disponibles")
    public ResponseEntity<List<TravelResponseDTO>> getAvailable() {
        return ResponseEntity.ok(travelService.getAvailable());
    }

    @GetMapping("/sale")
    @Operation(summary = "Obtener viajes en oferta")
    public ResponseEntity<List<TravelResponseDTO>> getOnSale() {
        return ResponseEntity.ok(travelService.getOnSale());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un viaje por ID")
    public ResponseEntity<TravelResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(travelService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo viaje")
    public ResponseEntity<TravelResponseDTO> create(@Valid @RequestBody TravelRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(travelService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un viaje existente")
    public ResponseEntity<TravelResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody TravelRequestDTO dto) {
        return ResponseEntity.ok(travelService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un viaje")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        travelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
