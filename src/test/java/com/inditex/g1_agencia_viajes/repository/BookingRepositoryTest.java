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
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Travel travel2025;
    private Travel travel2026;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        travel2025 = new Travel();
        travel2025.setDestiny("Paris");
        travel2025.setStartDate(LocalDate.of(2025, 6, 1));
        travel2025.setEndDate(LocalDate.of(2025, 6, 10));
        travel2025.setActive(true);
        travel2025.setAvailablePlaces(50);
        entityManager.persist(travel2025);

        travel2026 = new Travel();
        travel2026.setDestiny("London");
        travel2026.setStartDate(LocalDate.of(2026, 7, 15));
        travel2026.setEndDate(LocalDate.of(2026, 7, 22));
        travel2026.setActive(true);
        travel2026.setAvailablePlaces(30);
        entityManager.persist(travel2026);

        user1 = new User();
        user1.setName("Alice");
        user1.setSurname("Smith");
        user1.setEmail("alice@test.com");
        user1.setAge(30);
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("Bob");
        user2.setSurname("Jones");
        user2.setEmail("bob@test.com");
        user2.setAge(25);
        entityManager.persist(user2);

        user3 = new User();
        user3.setName("Charlie");
        user3.setSurname("Brown");
        user3.setEmail("charlie@test.com");
        user3.setAge(35);
        entityManager.persist(user3);
    }

    @Test
    void countTotalPassengersByTravelId_ShouldReturnCorrectCount() {
        Booking booking = new Booking();
        booking.setTravel(travel2025);
        booking.setTotalPrice(1500.0);
        booking.setBoughtDate(LocalDateTime.now());
        booking.setTypeBoard(TypeBoard.HALF);
        booking.setIsGroup(false);
        booking.setCustomers(List.of(user1, user2));
        entityManager.persistAndFlush(booking);
        entityManager.clear();

        int count = bookingRepository.countTotalPassengersByTravelId(travel2025.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    void countTotalPassengersByTravelId_WhenNoBookings_ShouldReturnZero() {
        int count = bookingRepository.countTotalPassengersByTravelId(travel2025.getId());

        assertThat(count).isEqualTo(0);
    }

    @Test
    void countTotalPassengersByTravelId_WithMultipleBookings_ShouldSumAll() {
        Booking booking1 = new Booking();
        booking1.setTravel(travel2025);
        booking1.setTotalPrice(1000.0);
        booking1.setBoughtDate(LocalDateTime.now());
        booking1.setTypeBoard(TypeBoard.HALF);
        booking1.setIsGroup(false);
        booking1.setCustomers(List.of(user1));
        entityManager.persistAndFlush(booking1);

        Booking booking2 = new Booking();
        booking2.setTravel(travel2025);
        booking2.setTotalPrice(2000.0);
        booking2.setBoughtDate(LocalDateTime.now());
        booking2.setTypeBoard(TypeBoard.FULL);
        booking2.setIsGroup(true);
        booking2.setCustomers(List.of(user2, user3));
        entityManager.persistAndFlush(booking2);
        entityManager.clear();

        int count = bookingRepository.countTotalPassengersByTravelId(travel2025.getId());

        assertThat(count).isEqualTo(3);
    }

    @Test
    void sumEarningsByTravelYear_ShouldReturnTotalEarnings() {
        Booking booking1 = new Booking();
        booking1.setTravel(travel2025);
        booking1.setTotalPrice(1000.0);
        booking1.setBoughtDate(LocalDateTime.now());
        booking1.setTypeBoard(TypeBoard.HALF);
        booking1.setIsGroup(false);
        entityManager.persistAndFlush(booking1);

        Booking booking2 = new Booking();
        booking2.setTravel(travel2025);
        booking2.setTotalPrice(500.0);
        booking2.setBoughtDate(LocalDateTime.now());
        booking2.setTypeBoard(TypeBoard.FULL);
        booking2.setIsGroup(false);
        entityManager.persistAndFlush(booking2);
        entityManager.clear();

        Double earnings = bookingRepository.sumEarningsByTravelYear(2025);

        assertThat(earnings).isEqualTo(1500.0);
    }

    @Test
    void sumEarningsByTravelYear_ShouldNotIncludeOtherYears() {
        Booking booking2025 = new Booking();
        booking2025.setTravel(travel2025);
        booking2025.setTotalPrice(1000.0);
        booking2025.setBoughtDate(LocalDateTime.now());
        booking2025.setTypeBoard(TypeBoard.HALF);
        booking2025.setIsGroup(false);
        entityManager.persistAndFlush(booking2025);

        Booking booking2026 = new Booking();
        booking2026.setTravel(travel2026);
        booking2026.setTotalPrice(2000.0);
        booking2026.setBoughtDate(LocalDateTime.now());
        booking2026.setTypeBoard(TypeBoard.FULL);
        booking2026.setIsGroup(false);
        entityManager.persistAndFlush(booking2026);
        entityManager.clear();

        Double earnings2025 = bookingRepository.sumEarningsByTravelYear(2025);
        Double earnings2026 = bookingRepository.sumEarningsByTravelYear(2026);

        assertThat(earnings2025).isEqualTo(1000.0);
        assertThat(earnings2026).isEqualTo(2000.0);
    }

    @Test
    void sumEarningsByTravelYear_WhenNoEarnings_ShouldReturnZero() {
        Double earnings = bookingRepository.sumEarningsByTravelYear(2024);

        assertThat(earnings).isEqualTo(0.0);
    }

    @Test
    void findTopTravelsByRevenue_ShouldReturnTop3() {
        // travel2025: 2 bookings = 1500 total -> should be second
        Booking booking1 = new Booking();
        booking1.setTravel(travel2025);
        booking1.setTotalPrice(1000.0);
        booking1.setBoughtDate(LocalDateTime.now());
        booking1.setTypeBoard(TypeBoard.HALF);
        booking1.setIsGroup(false);
        entityManager.persistAndFlush(booking1);

        Booking booking2 = new Booking();
        booking2.setTravel(travel2025);
        booking2.setTotalPrice(500.0);
        booking2.setBoughtDate(LocalDateTime.now());
        booking2.setTypeBoard(TypeBoard.FULL);
        booking2.setIsGroup(false);
        entityManager.persistAndFlush(booking2);

        // 2nd travel in 2025 with 1 booking = 2500 -> should be first
        Travel topTravel = new Travel();
        topTravel.setDestiny("Tokyo");
        topTravel.setStartDate(LocalDate.of(2025, 3, 1));
        topTravel.setEndDate(LocalDate.of(2025, 3, 15));
        topTravel.setActive(true);
        topTravel.setAvailablePlaces(20);
        entityManager.persist(topTravel);

        Booking topBooking = new Booking();
        topBooking.setTravel(topTravel);
        topBooking.setTotalPrice(2500.0);
        topBooking.setBoughtDate(LocalDateTime.now());
        topBooking.setTypeBoard(TypeBoard.HALF);
        topBooking.setIsGroup(false);
        entityManager.persistAndFlush(topBooking);

        // 3rd travel in 2025 with 1 booking = 800 -> should be third
        Travel thirdTravel = new Travel();
        thirdTravel.setDestiny("Berlin");
        thirdTravel.setStartDate(LocalDate.of(2025, 8, 1));
        thirdTravel.setEndDate(LocalDate.of(2025, 8, 10));
        thirdTravel.setActive(true);
        thirdTravel.setAvailablePlaces(15);
        entityManager.persist(thirdTravel);

        Booking thirdBooking = new Booking();
        thirdBooking.setTravel(thirdTravel);
        thirdBooking.setTotalPrice(800.0);
        thirdBooking.setBoughtDate(LocalDateTime.now());
        thirdBooking.setTypeBoard(TypeBoard.FULL);
        thirdBooking.setIsGroup(false);
        entityManager.persistAndFlush(thirdBooking);

        // 4th travel in 2025 with 1 booking = 300 -> should NOT be in top 3
        Travel fourthTravel = new Travel();
        fourthTravel.setDestiny("Rome");
        fourthTravel.setStartDate(LocalDate.of(2025, 10, 1));
        fourthTravel.setEndDate(LocalDate.of(2025, 10, 7));
        fourthTravel.setActive(true);
        fourthTravel.setAvailablePlaces(10);
        entityManager.persist(fourthTravel);

        Booking fourthBooking = new Booking();
        fourthBooking.setTravel(fourthTravel);
        fourthBooking.setTotalPrice(300.0);
        fourthBooking.setBoughtDate(LocalDateTime.now());
        fourthBooking.setTypeBoard(TypeBoard.HALF);
        fourthBooking.setIsGroup(false);
        entityManager.persistAndFlush(fourthBooking);
        entityManager.clear();

        List<Object[]> topTravels = bookingRepository.findTopTravelsByRevenue(2025);

        assertThat(topTravels).hasSize(3);
        assertThat(topTravels).extracting(
                row -> ((Number) row[0]).longValue(),
                row -> (String) row[1],
                row -> ((Number) row[2]).doubleValue()
        ).containsExactly(
                tuple(topTravel.getId(), "Tokyo", 2500.0),
                tuple(travel2025.getId(), "Paris", 1500.0),
                tuple(thirdTravel.getId(), "Berlin", 800.0)
        );
    }

    @Test
    void findTopTravelsByRevenue_WhenNoTravels_ShouldReturnEmpty() {
        List<Object[]> topTravels = bookingRepository.findTopTravelsByRevenue(2024);

        assertThat(topTravels).isEmpty();
    }

    @Test
    void findTopTravelsByRevenue_ShouldOnlyIncludeSpecifiedYear() {
        Booking booking2026 = new Booking();
        booking2026.setTravel(travel2026);
        booking2026.setTotalPrice(3000.0);
        booking2026.setBoughtDate(LocalDateTime.now());
        booking2026.setTypeBoard(TypeBoard.HALF);
        booking2026.setIsGroup(false);
        entityManager.persistAndFlush(booking2026);
        entityManager.clear();

        List<Object[]> topTravels2025 = bookingRepository.findTopTravelsByRevenue(2025);
        List<Object[]> topTravels2026 = bookingRepository.findTopTravelsByRevenue(2026);

        assertThat(topTravels2025).isEmpty();
        assertThat(topTravels2026).hasSize(1);
        assertThat(topTravels2026).extracting(row -> (String) row[1])
                .containsExactly("London");
    }
}
