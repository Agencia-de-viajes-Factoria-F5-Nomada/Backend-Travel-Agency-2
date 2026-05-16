package com.inditex.g1_agencia_viajes.controller;

import com.inditex.g1_agencia_viajes.dto.EmployeeRequestDTO;
import com.inditex.g1_agencia_viajes.dto.EmployeeResponseDTO;
import com.inditex.g1_agencia_viajes.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Empleados", description = "Gestión de empleados de la agencia")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los empleados")
    public ResponseEntity<Page<EmployeeResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un empleado por ID")
    public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo empleado")
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.saveEmployee(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un empleado existente")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un empleado")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
