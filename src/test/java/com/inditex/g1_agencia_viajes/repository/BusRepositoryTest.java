package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Bus;
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
class BusRepositoryTest {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Bus activeBus;
    private Bus inactiveBus;

    @BeforeEach
    void setUp() {
        activeBus = new Bus();
        activeBus.setLicensePlate("ABC-1234");
        activeBus.setCapacity(50);
        activeBus.setActive(true);
        entityManager.persist(activeBus);

        inactiveBus = new Bus();
        inactiveBus.setLicensePlate("XYZ-5678");
        inactiveBus.setCapacity(30);
        inactiveBus.setActive(false);
        entityManager.persist(inactiveBus);

        entityManager.flush();
    }

    @Test
    void existsByLicensePlate_WhenExists_ShouldReturnTrue() {
        boolean exists = busRepository.existsByLicensePlate("ABC-1234");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByLicensePlate_WhenNotExists_ShouldReturnFalse() {
        boolean exists = busRepository.existsByLicensePlate("NON-EXISTENT");

        assertThat(exists).isFalse();
    }

    @Test
    void findByActive_ShouldReturnActiveBuses() {
        List<Bus> result = busRepository.findByActive(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLicensePlate()).isEqualTo("ABC-1234");
    }

    @Test
    void findByActive_ShouldReturnInactiveBuses() {
        List<Bus> result = busRepository.findByActive(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLicensePlate()).isEqualTo("XYZ-5678");
    }

    @Test
    void findByActive_WithPageable_ShouldReturnPagedActiveBuses() {
        Page<Bus> result = busRepository.findByActive(true, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByActive_WhenNoBusesMatch_ShouldReturnEmpty() {
        busRepository.deleteAll();
        entityManager.flush();

        List<Bus> result = busRepository.findByActive(true);

        assertThat(result).isEmpty();
    }
}
