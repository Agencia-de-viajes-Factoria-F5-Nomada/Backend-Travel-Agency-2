package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT COUNT(*) FROM customers_bookings cb " +
           "JOIN bookings b ON cb.booking_id = b.booking_id " +
           "WHERE b.travels_id = :travelId", nativeQuery = true)
    int countTotalPassengersByTravelId(@Param("travelId") Long travelId);

    @Query(value = "SELECT COALESCE(SUM(b.total_price), 0) FROM bookings b " +
           "JOIN travels t ON b.travels_id = t.id " +
           "WHERE YEAR(t.start_date) = :year", nativeQuery = true)
    Double sumEarningsByTravelYear(@Param("year") int year);

    @Query(value = "SELECT t.id, t.destiny, COALESCE(SUM(b.total_price), 0) as revenue " +
           "FROM bookings b JOIN travels t ON b.travels_id = t.id " +
           "WHERE YEAR(t.start_date) = :year " +
           "GROUP BY t.id, t.destiny " +
           "ORDER BY revenue DESC LIMIT 3", nativeQuery = true)
    List<Object[]> findTopTravelsByRevenue(@Param("year") int year);

    Page<Booking> findByEmployeeEmployeeId(Long employeeId, Pageable pageable);

    Optional<Booking> findByBookingIdAndEmployeeEmployeeId(Long bookingId, Long employeeId);
}
