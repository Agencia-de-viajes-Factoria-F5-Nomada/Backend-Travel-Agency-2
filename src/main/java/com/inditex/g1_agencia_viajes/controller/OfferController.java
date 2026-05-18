package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.OfferRequestDTO;
import com.inditex.g1_agencia_viajes.dto.OfferResponseDTO;
import com.inditex.g1_agencia_viajes.service.OfferService;
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
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "Ofertas", description = "Gestión de ofertas y descuentos")
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    @Operation(summary = "Obtener todas las ofertas")
    public ResponseEntity<Page<OfferResponseDTO>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(offerService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una oferta por ID")
    public ResponseEntity<OfferResponseDTO> getById(@PathVariable Long id) {
        return offerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva oferta")
    public ResponseEntity<OfferResponseDTO> create(@Valid @RequestBody OfferRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.save(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una oferta existente")
    public ResponseEntity<OfferResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody OfferRequestDTO dto) {
        return ResponseEntity.ok(offerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una oferta")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        offerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
