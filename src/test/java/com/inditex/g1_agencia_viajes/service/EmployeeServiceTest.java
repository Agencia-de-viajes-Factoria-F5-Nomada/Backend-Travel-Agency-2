package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.EmployeeRequestDTO;
import com.inditex.g1_agencia_viajes.dto.EmployeeResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Gender;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository);

        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setEmail("john@nomada.es");
        employee.setGender(Gender.MALE);
        employee.setWorkHour(40);
        employee.setHired(true);
        employee.setRole(Role.EDITOR);
        employee.setPassword("$2a$10$hashedPassword");

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setName("John");
        requestDTO.setSurname("Doe");
        requestDTO.setEmail("john@nomada.es");
        requestDTO.setGender(Gender.MALE);
        requestDTO.setWorkHour(40);
        requestDTO.setHired(true);
        requestDTO.setRole(Role.EDITOR);
        requestDTO.setPassword("plainPassword");
    }

    @Test
    void saveEmployee_ShouldEncryptPasswordAndSave() {
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee saved = invocation.getArgument(0);
            saved.setEmployeeId(1L);
            return saved;
        });

        EmployeeResponseDTO result = employeeService.saveEmployee(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void saveEmployee_ShouldThrowWhenEmailNotFromNomadaDomain() {
        requestDTO.setEmail("john@gmail.com");

        assertThatThrownBy(() -> employeeService.saveEmployee(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@nomada.es");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_ShouldThrowWhenEmailIsNull() {
        requestDTO.setEmail(null);

        assertThatThrownBy(() -> employeeService.saveEmployee(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@nomada.es");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(employee)));

        Page<EmployeeResponseDTO> result = employeeService.getAllEmployees(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John");
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(1L);
    }

    @Test
    void getEmployeeById_ShouldThrowWhenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("el empleado");
    }

    @Test
    void deleteEmployee_ShouldSoftDelete() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        assertThat(employee.getActive()).isFalse();
        verify(employeeRepository).save(employee);
    }

    @Test
    void deleteEmployee_ShouldThrowWhenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.deleteEmployee(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("el empleado");

        verify(employeeRepository, never()).save(any());
    }
}
