package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import com.inditex.g1_agencia_viajes.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
@Tag(name = "Autobuses", description = "Gestión de autobuses")
public class BusController {

    private final BusService busService;

    @GetMapping
    @Operation(summary = "Obtener todos los autobuses")
    public ResponseEntity<Page<BusResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(busService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un autobús por ID")
    public ResponseEntity<BusResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo autobús")
    public ResponseEntity<BusResponseDTO> create(@Valid @RequestBody BusRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un autobús existente")
    public ResponseEntity<BusResponseDTO> update(@PathVariable Long id, @Valid @RequestBody BusRequestDTO dto) {
        return ResponseEntity.ok(busService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un autobús")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        busService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
