package com.inditex.g1_agencia_viajes.config;

import com.inditex.g1_agencia_viajes.model.Employee;
import com.inditex.g1_agencia_viajes.model.Gender;
import com.inditex.g1_agencia_viajes.model.Role;
import com.inditex.g1_agencia_viajes.repository.EmployeeRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_PASSWORD = "123456";

    private final EmployeeRepository employeeRepository;

    public DataInitializer(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() > 0) {
            log.info("La base de datos ya contiene empleados. No se insertan datos iniciales.");
            return;
        }

        log.info("Creando empleados iniciales...");

        createEmployee("Carlos", "Perez", "carlos@nomada.es", Gender.MALE, 40, true, Role.ADMIN);
        createEmployee("Ana", "Sanchez", "ana@nomada.es", Gender.FEMALE, 35, true, Role.EMPLOYEE);
        createEmployee("Sofia", "Oliveira", "sofia@nomada.es", Gender.FEMALE, 40, true, Role.EMPLOYEE);
        createEmployee("David", "Thimotheo", "david@nomada.es", Gender.MALE, 20, true, Role.EMPLOYEE);

        log.info("4 empleados iniciales creados correctamente.");
    }

    private void createEmployee(String name, String surname, String email, Gender gender,
                                int workHour, boolean hired, Role role) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setSurname(surname);
        employee.setEmail(email);
        employee.setGender(gender);
        employee.setWorkHour(workHour);
        employee.setHired(hired);
        employee.setRole(role);
        employee.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD, BCrypt.gensalt()));
        employeeRepository.save(employee);
        log.info("Empleado creado: {} ({}) - Rol: {}", name, email, role);
    }
}
