package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.EmployeeRequestDTO;
import com.inditex.g1_agencia_viajes.dto.EmployeeResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.EmployeeMapper;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final String EMAIL_DOMAIN = "@nomada.es";

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponseDTO saveEmployee(EmployeeRequestDTO dto) {
        validateEmailDomain(dto.getEmail());

        Employee employee = employeeMapper.toEntity(dto);
        employee.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));

        return employeeMapper.toDTO(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el empleado", id));

        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            validateEmailDomain(dto.getEmail());
        }

        employeeMapper.updateFromDto(dto, existing);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        }

        return employeeMapper.toDTO(employeeRepository.save(existing));
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el empleado", id));
        return employeeMapper.toDTO(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el empleado", id));
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    private void validateEmailDomain(String email) {
        if (email == null || !email.toLowerCase().endsWith(EMAIL_DOMAIN)) {
            throw new IllegalArgumentException("El email debe ser del dominio " + EMAIL_DOMAIN);
        }
    }
}
