package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Bus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    boolean existsByLicensePlate(String licensePlate);

    List<Bus> findByActive(Boolean active);

    Page<Bus> findByActive(Boolean active, Pageable pageable);

    List<Bus> findByAvailablePlacesGreaterThan(Integer places);

    Page<Bus> findByAvailablePlacesGreaterThan(Integer places, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bus b WHERE b.id = :id")
    Optional<Bus> findByIdForUpdate(Long id);
}
