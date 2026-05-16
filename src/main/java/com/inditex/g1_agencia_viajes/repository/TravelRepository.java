package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Travel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {

    List<Travel> findByActiveTrue();

    Page<Travel> findByActiveTrue(Pageable pageable);

    List<Travel> findBySaleTrueAndActiveTrueAndStartDateAfter(LocalDate date);

    Page<Travel> findBySaleTrueAndActiveTrueAndStartDateAfter(LocalDate date, Pageable pageable);

    List<Travel> findByActiveTrueAndStartDateAfter(LocalDate date);

    Page<Travel> findByActiveTrueAndStartDateAfter(LocalDate date, Pageable pageable);

    Page<Travel> findByActiveTrueAndStartDateAfterAndAvailablePlacesGreaterThan(LocalDate date, Integer places, Pageable pageable);

    @Query(value = "SELECT YEAR(t.start_date) as yr, COUNT(*) as count " +
           "FROM travels t WHERE t.active = true " +
           "GROUP BY YEAR(t.start_date) ORDER BY yr DESC", nativeQuery = true)
    List<Object[]> countTravelsPerYear();
}