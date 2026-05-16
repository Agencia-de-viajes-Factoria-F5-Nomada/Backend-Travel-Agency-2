package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.DriverRequestDTO;
import com.inditex.g1_agencia_viajes.dto.DriverResponseDTO;
import com.inditex.g1_agencia_viajes.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Conductores", description = "Gestión de conductores")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo conductor")
    public ResponseEntity<DriverResponseDTO> create(@Valid @RequestBody DriverRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los conductores")
    public ResponseEntity<List<DriverResponseDTO>> getAll() {
        return ResponseEntity.ok(driverService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un conductor por ID")
    public ResponseEntity<DriverResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getById(id));
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener conductores activos")
    public ResponseEntity<List<DriverResponseDTO>> getActive() {
        return ResponseEntity.ok(driverService.getActive());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un conductor existente")
    public ResponseEntity<DriverResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody DriverRequestDTO dto) {
        return ResponseEntity.ok(driverService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un conductor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
