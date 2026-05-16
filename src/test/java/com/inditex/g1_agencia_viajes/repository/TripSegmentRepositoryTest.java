package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class TripSegmentRepositoryTest {

    @Autowired
    private TripSegmentRepository tripSegmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Driver driver;
    private Driver otherDriver;
    private Travel travel;
    private Bus bus;

    @BeforeEach
    void setUp() {
        driver = new Driver();
        driver.setName("John Driver");
        driver.setPhone("+123456789");
        driver.setLicenceActive(true);
        entityManager.persist(driver);

        otherDriver = new Driver();
        otherDriver.setName("Jane Driver");
        otherDriver.setPhone("+987654321");
        otherDriver.setLicenceActive(true);
        entityManager.persist(otherDriver);

        bus = new Bus();
        bus.setLicensePlate("ABC-1234");
        bus.setCapacity(50);
        bus.setActive(true);
        entityManager.persist(bus);

        travel = new Travel();
        travel.setDestiny("Paris");
        travel.setStartDate(LocalDate.of(2025, 6, 15));
        travel.setEndDate(LocalDate.of(2025, 6, 22));
        travel.setActive(true);
        travel.setAvailablePlaces(30);
        entityManager.persist(travel);
    }

    @Test
    void findOverlappingByDriver_WhenTimesOverlap_ShouldReturnSegments() {
        TripSegment segment = new TripSegment();
        segment.setTravel(travel);
        segment.setOrigin("Madrid");
        segment.setDestination("Barcelona");
        segment.setStartTime(LocalDateTime.of(2025, 6, 15, 8, 0));
        segment.setEndTime(LocalDateTime.of(2025, 6, 15, 12, 0));
        segment.setDriver(driver);
        segment.setBus(bus);
        entityManager.persist(segment);
        entityManager.flush();

        // Query with overlapping range (10:00 - 14:00 overlaps with 8:00 - 12:00)
        List<TripSegment> result = tripSegmentRepository.findOverlappingByDriver(
                driver,
                LocalDateTime.of(2025, 6, 15, 10, 0),
                LocalDateTime.of(2025, 6, 15, 14, 0)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrigin()).isEqualTo("Madrid");
    }

    @Test
    void findOverlappingByDriver_WhenQueryInsideExistingRange_ShouldReturnSegments() {
        TripSegment segment = new TripSegment();
        segment.setTravel(travel);
        segment.setOrigin("Madrid");
        segment.setDestination("Barcelona");
        segment.setStartTime(LocalDateTime.of(2025, 6, 15, 8, 0));
        segment.setEndTime(LocalDateTime.of(2025, 6, 15, 18, 0));
        segment.setDriver(driver);
        segment.setBus(bus);
        entityManager.persist(segment);
        entityManager.flush();

        // Query range (10:00 - 14:00) is inside existing range (8:00 - 18:00) -> overlap
        List<TripSegment> result = tripSegmentRepository.findOverlappingByDriver(
                driver,
                LocalDateTime.of(2025, 6, 15, 10, 0),
                LocalDateTime.of(2025, 6, 15, 14, 0)
        );

        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingByDriver_WhenNoOverlap_ShouldReturnEmpty() {
        TripSegment segment = new TripSegment();
        segment.setTravel(travel);
        segment.setOrigin("Madrid");
        segment.setDestination("Barcelona");
        segment.setStartTime(LocalDateTime.of(2025, 6, 15, 8, 0));
        segment.setEndTime(LocalDateTime.of(2025, 6, 15, 12, 0));
        segment.setDriver(driver);
        segment.setBus(bus);
        entityManager.persist(segment);
        entityManager.flush();

        // Query range (14:00 - 18:00) does NOT overlap with (8:00 - 12:00)
        List<TripSegment> result = tripSegmentRepository.findOverlappingByDriver(
                driver,
                LocalDateTime.of(2025, 6, 15, 14, 0),
                LocalDateTime.of(2025, 6, 15, 18, 0)
        );

        assertThat(result).isEmpty();
    }

    @Test
    void findOverlappingByDriver_WithDifferentDriver_ShouldReturnEmpty() {
        TripSegment segment = new TripSegment();
        segment.setTravel(travel);
        segment.setOrigin("Madrid");
        segment.setDestination("Barcelona");
        segment.setStartTime(LocalDateTime.of(2025, 6, 15, 8, 0));
        segment.setEndTime(LocalDateTime.of(2025, 6, 15, 12, 0));
        segment.setDriver(driver);
        segment.setBus(bus);
        entityManager.persist(segment);
        entityManager.flush();

        // Query same time range but different driver
        List<TripSegment> result = tripSegmentRepository.findOverlappingByDriver(
                otherDriver,
                LocalDateTime.of(2025, 6, 15, 8, 0),
                LocalDateTime.of(2025, 6, 15, 12, 0)
        );

        assertThat(result).isEmpty();
    }

    @Test
    void findOverlappingByDriver_WithExactMatch_ShouldReturnSegments() {
        TripSegment segment = new TripSegment();
        segment.setTravel(travel);
        segment.setOrigin("Madrid");
        segment.setDestination("Barcelona");
        segment.setStartTime(LocalDateTime.of(2025, 6, 15, 8, 0));
        segment.setEndTime(LocalDateTime.of(2025, 6, 15, 12, 0));
        segment.setDriver(driver);
        segment.setBus(bus);
        entityManager.persist(segment);
        entityManager.flush();

        // Query with exact same time range
        List<TripSegment> result = tripSegmentRepository.findOverlappingByDriver(
                driver,
                LocalDateTime.of(2025, 6, 15, 8, 0),
                LocalDateTime.of(2025, 6, 15, 12, 0)
        );

        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingByDriver_WhenNoSegments_ShouldReturnEmpty() {
        List<TripSegment> result = tripSegmentRepository.findOverlappingByDriver(
                driver,
                LocalDateTime.of(2025, 6, 15, 8, 0),
                LocalDateTime.of(2025, 6, 15, 12, 0)
        );

        assertThat(result).isEmpty();
    }

    @Test
    void findByTravelId_ShouldReturnSegmentsForTravel() {
        TripSegment segment1 = new TripSegment();
        segment1.setTravel(travel);
        segment1.setOrigin("Madrid");
        segment1.setDestination("Barcelona");
        segment1.setStartTime(LocalDateTime.of(2025, 6, 15, 8, 0));
        segment1.setEndTime(LocalDateTime.of(2025, 6, 15, 12, 0));
        segment1.setDriver(driver);
        segment1.setBus(bus);
        entityManager.persist(segment1);

        TripSegment segment2 = new TripSegment();
        segment2.setTravel(travel);
        segment2.setOrigin("Barcelona");
        segment2.setDestination("Paris");
        segment2.setStartTime(LocalDateTime.of(2025, 6, 15, 13, 0));
        segment2.setEndTime(LocalDateTime.of(2025, 6, 15, 18, 0));
        segment2.setDriver(driver);
        segment2.setBus(bus);
        entityManager.persist(segment2);
        entityManager.flush();

        List<TripSegment> result = tripSegmentRepository.findByTravelId(travel.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TripSegment::getDestination)
                .containsExactly("Barcelona", "Paris");
    }

    @Test
    void findByTravelId_WhenNoSegments_ShouldReturnEmpty() {
        List<TripSegment> result = tripSegmentRepository.findByTravelId(999L);

        assertThat(result).isEmpty();
    }
}
