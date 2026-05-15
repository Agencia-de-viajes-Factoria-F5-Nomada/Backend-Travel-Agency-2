package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Gender;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    private Employee employee;

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
        employee.setPassword("plainPassword");
    }

    @Test
    void saveEmployee_ShouldEncryptPasswordAndSave() {
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee saved = invocation.getArgument(0);
            saved.setEmployeeId(1L);
            return saved;
        });

        Employee result = employeeService.saveEmployee(employee);

        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isNotEqualTo("plainPassword");
        assertThat(BCrypt.checkpw("plainPassword", result.getPassword())).isTrue();
        verify(employeeRepository).save(employee);
    }

    @Test
    void saveEmployee_ShouldThrowWhenEmailNotFromNomadaDomain() {
        employee.setEmail("john@gmail.com");

        assertThatThrownBy(() -> employeeService.saveEmployee(employee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@nomada.es");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void saveEmployee_ShouldThrowWhenEmailIsNull() {
        employee.setEmail(null);

        assertThatThrownBy(() -> employeeService.saveEmployee(employee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@nomada.es");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void getAllEmployees_ShouldReturnAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John");
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(1L);
    }

    @Test
    void getEmployeeById_ShouldReturnNullWhenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        Employee result = employeeService.getEmployeeById(99L);

        assertThat(result).isNull();
    }

    @Test
    void deleteEmployee_ShouldDeleteEmployee() {
        employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
    }
}
