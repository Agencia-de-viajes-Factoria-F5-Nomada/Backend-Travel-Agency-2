package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Gender;
import com.inditex.g1_agencia_viajes.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setName("Admin");
        employee.setSurname("User");
        employee.setEmail("admin@travel.com");
        employee.setGender(Gender.MALE);
        employee.setHired(true);
        employee.setRole(Role.ADMIN);
        employee.setPassword("$2a$10$hashedpassword");
        entityManager.persist(employee);
        entityManager.flush();
    }

    @Test
    void findByEmail_WhenExists_ShouldReturnEmployee() {
        Optional<Employee> result = employeeRepository.findByEmail("admin@travel.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Admin");
    }

    @Test
    void findByEmail_WhenNotExists_ShouldReturnEmpty() {
        Optional<Employee> result = employeeRepository.findByEmail("nonexistent@travel.com");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_WhenExists_ShouldReturnTrue() {
        boolean exists = employeeRepository.existsByEmail("admin@travel.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WhenNotExists_ShouldReturnFalse() {
        boolean exists = employeeRepository.existsByEmail("nonexistent@travel.com");

        assertThat(exists).isFalse();
    }
}
