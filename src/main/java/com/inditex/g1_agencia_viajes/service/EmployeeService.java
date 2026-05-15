package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private static final String EMAIL_DOMAIN = "@nomada.es";

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        validateEmailDomain(employee.getEmail());
        String passwordPlain = employee.getPassword();
        String encryptedPassword = BCrypt.hashpw(passwordPlain, BCrypt.gensalt());
        employee.setPassword(encryptedPassword);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee details) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("l empleado", id));

        if (details.getEmail() != null && !details.getEmail().equals(existing.getEmail())) {
            validateEmailDomain(details.getEmail());
            existing.setEmail(details.getEmail());
        }

        existing.setName(details.getName());
        existing.setSurname(details.getSurname());
        existing.setGender(details.getGender());
        existing.setWorkHour(details.getWorkHour());
        existing.setHired(details.getHired());
        existing.setRole(details.getRole());

        if (details.getPassword() != null && !details.getPassword().isBlank()) {
            existing.setPassword(BCrypt.hashpw(details.getPassword(), BCrypt.gensalt()));
        }

        return employeeRepository.save(existing);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    private void validateEmailDomain(String email) {
        if (email == null || !email.toLowerCase().endsWith(EMAIL_DOMAIN)) {
            throw new IllegalArgumentException("El email debe ser del dominio " + EMAIL_DOMAIN);
        }
    }
}
