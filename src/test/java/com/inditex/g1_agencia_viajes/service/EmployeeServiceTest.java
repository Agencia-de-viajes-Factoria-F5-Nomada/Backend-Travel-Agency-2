package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.EmployeeRequestDTO;
import com.inditex.g1_agencia_viajes.dto.EmployeeResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ForbiddenAccessException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.EmployeeMapper;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Gender;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private EmployeeMapper employeeMapper;

    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, employeeMapper);

        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("John");
        employee.setSurname("Doe");
        employee.setEmail("john@nomada.es");
        employee.setGender(Gender.MALE);
        employee.setWorkHour(40);
        employee.setHired(true);
        employee.setRole(Role.EMPLOYEE);
        employee.setPassword("$2a$10$hashedPassword");

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setName("John");
        requestDTO.setSurname("Doe");
        requestDTO.setEmail("john@nomada.es");
        requestDTO.setGender(Gender.MALE);
        requestDTO.setWorkHour(40);
        requestDTO.setHired(true);
        requestDTO.setRole(Role.EMPLOYEE);
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
    void getAllEmployees_WhenAdmin_ShouldReturnAllEmployees() {
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(employee)));

        Page<EmployeeResponseDTO> result = employeeService.getAllEmployees(Pageable.unpaged(), 1L, Role.ADMIN);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("John");
    }

    @Test
    void getAllEmployees_WhenEmployee_ShouldReturnOnlySelf() {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(1L);
        dto.setName("John");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDTO(employee)).thenReturn(dto);

        Page<EmployeeResponseDTO> result = employeeService.getAllEmployees(Pageable.unpaged(), 1L, Role.EMPLOYEE);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getEmployeeId()).isEqualTo(1L);
    }

    @Test
    void getEmployeeById_WhenAdmin_ShouldReturnEmployee() {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDTO(employee)).thenReturn(dto);

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L, 2L, Role.ADMIN);

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(1L);
    }

    @Test
    void getEmployeeById_WhenEmployeeOwnId_ShouldReturnEmployee() {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDTO(employee)).thenReturn(dto);

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L, 1L, Role.EMPLOYEE);

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(1L);
    }

    @Test
    void getEmployeeById_WhenEmployeeOtherId_ShouldThrowForbidden() {
        assertThatThrownBy(() -> employeeService.getEmployeeById(2L, 1L, Role.EMPLOYEE))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("No tienes permiso para ver los datos de otro empleado");

        verify(employeeRepository, never()).findById(any());
    }

    @Test
    void getEmployeeById_WhenNotFound_ShouldThrowResourceNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99L, 1L, Role.ADMIN))
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
