package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByActive(Boolean active);

    Page<User> findByActive(Boolean active, Pageable pageable);

    boolean existsByEmail(String email);
}