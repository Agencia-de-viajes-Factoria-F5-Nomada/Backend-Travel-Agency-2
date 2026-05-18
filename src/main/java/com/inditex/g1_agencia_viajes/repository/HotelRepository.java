package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Hotel;
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
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByActive(Boolean active);

    Page<Hotel> findByActive(Boolean active, Pageable pageable);

    List<Hotel> findByCity(String city);

    List<Hotel> findByCountry(String country);

    List<Hotel> findByStars(Integer stars);

    List<Hotel> findByAvailablePlacesGreaterThan(Integer places);

    Page<Hotel> findByAvailablePlacesGreaterThan(Integer places, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM Hotel h WHERE h.id = :id")
    Optional<Hotel> findByIdForUpdate(Long id);
}