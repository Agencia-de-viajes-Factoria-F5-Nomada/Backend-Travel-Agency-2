package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.User;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User activeUser;
    private User inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = new User();
        activeUser.setName("Alice");
        activeUser.setSurname("Smith");
        activeUser.setEmail("alice@test.com");
        activeUser.setAge(30);
        activeUser.setActive(true);
        entityManager.persist(activeUser);

        inactiveUser = new User();
        inactiveUser.setName("Bob");
        inactiveUser.setSurname("Jones");
        inactiveUser.setEmail("bob@test.com");
        inactiveUser.setAge(25);
        inactiveUser.setActive(false);
        entityManager.persist(inactiveUser);

        entityManager.flush();
    }

    @Test
    void findByEmail_WhenExists_ShouldReturnUser() {
        Optional<User> result = userRepository.findByEmail("alice@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice");
    }

    @Test
    void findByEmail_WhenNotExists_ShouldReturnEmpty() {
        Optional<User> result = userRepository.findByEmail("nonexistent@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_WhenExists_ShouldReturnTrue() {
        boolean exists = userRepository.existsByEmail("alice@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WhenNotExists_ShouldReturnFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@test.com");

        assertThat(exists).isFalse();
    }

    @Test
    void findByActive_ShouldReturnActiveUsers() {
        List<User> result = userRepository.findByActive(true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    void findByActive_ShouldReturnInactiveUsers() {
        List<User> result = userRepository.findByActive(false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("bob@test.com");
    }

    @Test
    void findByActive_WithPageable_ShouldReturnPagedActiveUsers() {
        Page<User> result = userRepository.findByActive(true, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByEmail_ShouldBeCaseSensitive() {
        Optional<User> result = userRepository.findByEmail("ALICE@test.com");

        assertThat(result).isEmpty();
    }
}
