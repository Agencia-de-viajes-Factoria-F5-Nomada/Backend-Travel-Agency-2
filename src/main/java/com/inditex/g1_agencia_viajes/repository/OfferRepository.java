package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Offer;
import com.inditex.g1_agencia_viajes.model.OfferType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Page<Offer> findByOfferType(OfferType offerType, Pageable pageable);
}
