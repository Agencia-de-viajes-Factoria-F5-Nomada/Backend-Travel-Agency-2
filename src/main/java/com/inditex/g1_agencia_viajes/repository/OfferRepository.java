package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
}
