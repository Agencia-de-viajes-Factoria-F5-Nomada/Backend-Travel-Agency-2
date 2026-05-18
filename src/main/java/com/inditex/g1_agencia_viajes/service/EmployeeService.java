package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.EmployeeRequestDTO;
import com.inditex.g1_agencia_viajes.dto.EmployeeResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private static final String EMAIL_DOMAIN = "@nomada.es";

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponseDTO saveEmployee(EmployeeRequestDTO dto) {
        validateEmailDomain(dto.getEmail());

        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setSurname(dto.getSurname());
        employee.setEmail(dto.getEmail());
        employee.setGender(dto.getGender());
        employee.setWorkHour(dto.getWorkHour());
        employee.setHired(dto.getHired());
        employee.setRole(dto.getRole());
        employee.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));

        return toResponseDTO(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el empleado", id));

        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            validateEmailDomain(dto.getEmail());
            existing.setEmail(dto.getEmail());
        }

        existing.setName(dto.getName());
        existing.setSurname(dto.getSurname());
        existing.setGender(dto.getGender());
        existing.setWorkHour(dto.getWorkHour());
        existing.setHired(dto.getHired());
        existing.setRole(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        }

        return toResponseDTO(employeeRepository.save(existing));
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el empleado", id));
        return toResponseDTO(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el empleado", id));
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setName(employee.getName());
        dto.setSurname(employee.getSurname());
        dto.setEmail(employee.getEmail());
        dto.setGender(employee.getGender());
        dto.setWorkHour(employee.getWorkHour());
        dto.setHired(employee.getHired());
        dto.setRole(employee.getRole());
        dto.setActive(employee.getActive());
        return dto;
    }

    private void validateEmailDomain(String email) {
        if (email == null || !email.toLowerCase().endsWith(EMAIL_DOMAIN)) {
            throw new IllegalArgumentException("El email debe ser del dominio " + EMAIL_DOMAIN);
        }
    }
}
