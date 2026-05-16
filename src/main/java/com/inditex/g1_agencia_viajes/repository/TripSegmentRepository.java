package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Driver;
import com.inditex.g1_agencia_viajes.model.TripSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripSegmentRepository extends JpaRepository<TripSegment, Long> {

    List<TripSegment> findByTravelId(Long travelId);

    @Query("SELECT ts FROM TripSegment ts WHERE ts.driver = :driver " +
           "AND ((ts.startTime <= :endTime AND ts.endTime >= :startTime))")
    List<TripSegment> findOverlappingByDriver(
            @Param("driver") Driver driver,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
