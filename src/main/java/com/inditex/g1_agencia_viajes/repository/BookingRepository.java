package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT COUNT(*) FROM customers_bookings cb " +
           "JOIN bookings b ON cb.booking_id = b.booking_id " +
           "WHERE b.travels_id = :travelId", nativeQuery = true)
    int countTotalPassengersByTravelId(@Param("travelId") Long travelId);
}
