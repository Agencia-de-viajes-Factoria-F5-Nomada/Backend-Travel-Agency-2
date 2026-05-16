package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Bus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    boolean existsByLicensePlate(String licensePlate);

    List<Bus> findByActive(Boolean active);

    Page<Bus> findByActive(Boolean active, Pageable pageable);
}