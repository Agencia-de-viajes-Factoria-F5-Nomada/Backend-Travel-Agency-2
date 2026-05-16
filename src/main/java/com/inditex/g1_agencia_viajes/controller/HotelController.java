package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.HotelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.HotelResponseDTO;
import com.inditex.g1_agencia_viajes.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@Tag(name = "Hoteles", description = "Gestión de hoteles")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo hotel")
    public ResponseEntity<HotelResponseDTO> create(@Valid @RequestBody HotelRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hotelService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los hoteles")
    public ResponseEntity<List<HotelResponseDTO>> getAll() {
        return ResponseEntity.ok(hotelService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un hotel por ID")
    public ResponseEntity<HotelResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getById(id));
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener hoteles activos")
    public ResponseEntity<List<HotelResponseDTO>> getActive() {
        return ResponseEntity.ok(hotelService.getActive());
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener hoteles con plazas disponibles")
    public ResponseEntity<List<HotelResponseDTO>> getAvailable() {
        return ResponseEntity.ok(hotelService.getAvailable());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un hotel existente")
    public ResponseEntity<HotelResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody HotelRequestDTO dto) {
        return ResponseEntity.ok(hotelService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un hotel")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hotelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
