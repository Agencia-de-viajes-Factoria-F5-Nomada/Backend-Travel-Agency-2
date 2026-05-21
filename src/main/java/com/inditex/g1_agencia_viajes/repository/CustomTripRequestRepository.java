package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.CustomTripRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomTripRequestRepository extends JpaRepository<CustomTripRequest, Long> {
}