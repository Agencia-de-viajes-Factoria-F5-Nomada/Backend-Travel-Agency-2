package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Driver activeDriver;
    private Driver inactiveDriver;

    @BeforeEach
    void setUp() {
        activeDriver = new Driver();
        activeDriver.setName("John Active");
        activeDriver.setPhone("+111111111");
        activeDriver.setLicenceActive(true);
        activeDriver.setActive(true);
        entityManager.persist(activeDriver);

        inactiveDriver = new Driver();
        inactiveDriver.setName("Jane Inactive");
        inactiveDriver.setPhone("+222222222");
        inactiveDriver.setLicenceActive(false);
        inactiveDriver.setActive(true);
        entityManager.persist(inactiveDriver);

        entityManager.flush();
    }

    @Test
    void findByLicenceActive_ShouldReturnLicensedDrivers() {
        List<Driver> result = driverRepository.findByLicenceActive(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Active");
    }

    @Test
    void findByLicenceActive_ShouldReturnUnlicensedDrivers() {
        List<Driver> result = driverRepository.findByLicenceActive(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jane Inactive");
    }

    @Test
    void findByLicenceActive_WithPageable_ShouldReturnPagedResults() {
        Page<Driver> result = driverRepository.findByLicenceActive(true, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByActive_ShouldReturnActiveDrivers() {
        Driver inactiveAccountDriver = new Driver();
        inactiveAccountDriver.setName("Deleted Driver");
        inactiveAccountDriver.setPhone("+333333333");
        inactiveAccountDriver.setLicenceActive(true);
        inactiveAccountDriver.setActive(false);
        entityManager.persist(inactiveAccountDriver);
        entityManager.flush();

        List<Driver> result = driverRepository.findByActive(true);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Driver::getName)
                .containsExactlyInAnyOrder("John Active", "Jane Inactive");
    }

    @Test
    void findByLicenceActive_WhenNoDrivers_ShouldReturnEmpty() {
        driverRepository.deleteAll();
        entityManager.flush();

        List<Driver> result = driverRepository.findByLicenceActive(true);

        assertThat(result).isEmpty();
    }
}
