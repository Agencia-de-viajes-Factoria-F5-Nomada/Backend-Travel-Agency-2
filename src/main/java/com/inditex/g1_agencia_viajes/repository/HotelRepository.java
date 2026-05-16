package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByActive(Boolean active);

    Page<Hotel> findByActive(Boolean active, Pageable pageable);

    List<Hotel> findByCity(String city);

    List<Hotel> findByCountry(String country);

    List<Hotel> findByStars(Integer stars);

    List<Hotel> findByAvailablePlacesGreaterThan(Integer places);

    Page<Hotel> findByAvailablePlacesGreaterThan(Integer places, Pageable pageable);
}