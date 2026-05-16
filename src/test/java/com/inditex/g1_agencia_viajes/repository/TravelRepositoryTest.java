package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.Travel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class TravelRepositoryTest {

    @Autowired
    private TravelRepository travelRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Travel activeTravel;
    private Travel inactiveTravel;
    private Travel futureTravel;
    private Travel pastTravel;
    private Travel saleTravel;
    private Travel noPlacesTravel;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setName("Hotel Central");
        hotel.setCity("Madrid");
        hotel.setCountry("Spain");
        hotel.setStars(4);
        hotel.setAddress("Calle Mayor 1");
        hotel.setCapacity(100);
        hotel.setAvailablePlaces(80);
        entityManager.persist(hotel);

        activeTravel = new Travel();
        activeTravel.setDestiny("Paris");
        activeTravel.setStartDate(LocalDate.now().plusDays(5));
        activeTravel.setEndDate(LocalDate.of(2025, 6, 22));
        activeTravel.setActive(true);
        activeTravel.setSale(false);
        activeTravel.setAvailablePlaces(20);
        activeTravel.setHotel(hotel);
        entityManager.persist(activeTravel);

        inactiveTravel = new Travel();
        inactiveTravel.setDestiny("London");
        inactiveTravel.setStartDate(LocalDate.of(2025, 7, 1));
        inactiveTravel.setEndDate(LocalDate.of(2025, 7, 10));
        inactiveTravel.setActive(false);
        inactiveTravel.setSale(false);
        inactiveTravel.setAvailablePlaces(15);
        inactiveTravel.setHotel(hotel);
        entityManager.persist(inactiveTravel);

        futureTravel = new Travel();
        futureTravel.setDestiny("Tokyo");
        futureTravel.setStartDate(LocalDate.now().plusDays(30));
        futureTravel.setEndDate(LocalDate.now().plusDays(37));
        futureTravel.setActive(true);
        futureTravel.setSale(false);
        futureTravel.setAvailablePlaces(25);
        futureTravel.setHotel(hotel);
        entityManager.persist(futureTravel);

        pastTravel = new Travel();
        pastTravel.setDestiny("Berlin");
        pastTravel.setStartDate(LocalDate.now().minusDays(30));
        pastTravel.setEndDate(LocalDate.now().minusDays(23));
        pastTravel.setActive(true);
        pastTravel.setSale(false);
        pastTravel.setAvailablePlaces(10);
        pastTravel.setHotel(hotel);
        entityManager.persist(pastTravel);

        saleTravel = new Travel();
        saleTravel.setDestiny("Rome");
        saleTravel.setStartDate(LocalDate.now().plusDays(10));
        saleTravel.setEndDate(LocalDate.now().plusDays(17));
        saleTravel.setActive(true);
        saleTravel.setSale(true);
        saleTravel.setAvailablePlaces(8);
        saleTravel.setHotel(hotel);
        entityManager.persist(saleTravel);

        noPlacesTravel = new Travel();
        noPlacesTravel.setDestiny("New York");
        noPlacesTravel.setStartDate(LocalDate.now().plusDays(60));
        noPlacesTravel.setEndDate(LocalDate.now().plusDays(67));
        noPlacesTravel.setActive(true);
        noPlacesTravel.setSale(false);
        noPlacesTravel.setAvailablePlaces(0);
        noPlacesTravel.setHotel(hotel);
        entityManager.persist(noPlacesTravel);
    }

    @Test
    void findByActiveTrue_ShouldReturnOnlyActiveTravels() {
        List<Travel> result = travelRepository.findByActiveTrue();

        assertThat(result)
                .hasSize(5)
                .extracting(Travel::getId)
                .doesNotContain(inactiveTravel.getId());
    }

    @Test
    void findByActiveTrue_WithPageable_ShouldReturnPagedActiveTravels() {
        Page<Travel> result = travelRepository.findByActiveTrue(PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    @Test
    void findByActiveTrueAndStartDateAfter_ShouldReturnFutureActiveTravels() {
        List<Travel> result = travelRepository.findByActiveTrueAndStartDateAfter(LocalDate.now());

        assertThat(result)
                .hasSize(4)
                .extracting(Travel::getDestiny)
                .containsExactlyInAnyOrder("Tokyo", "Rome", "New York", "Paris")
                .doesNotContain("Berlin", "London");
    }

    @Test
    void findByActiveTrueAndStartDateAfter_WithPageable_ShouldReturnPagedResults() {
        Page<Travel> result = travelRepository.findByActiveTrueAndStartDateAfter(
                LocalDate.now(), PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    @Test
    void findBySaleTrueAndActiveTrueAndStartDateAfter_ShouldReturnOnSaleFutureActive() {
        List<Travel> result = travelRepository.findBySaleTrueAndActiveTrueAndStartDateAfter(LocalDate.now());

        assertThat(result)
                .hasSize(1)
                .extracting(Travel::getDestiny)
                .containsExactly("Rome");
    }

    @Test
    void findBySaleTrueAndActiveTrueAndStartDateAfter_WithPageable_ShouldReturnPagedResults() {
        Page<Travel> result = travelRepository.findBySaleTrueAndActiveTrueAndStartDateAfter(
                LocalDate.now(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByActiveTrueAndStartDateAfterAndAvailablePlacesGreaterThan_ShouldFilterCorrectly() {
        Page<Travel> result = travelRepository.findByActiveTrueAndStartDateAfterAndAvailablePlacesGreaterThan(
                LocalDate.now(), 0, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Travel::getDestiny)
                .containsExactlyInAnyOrder("Tokyo", "Rome", "Paris")
                .doesNotContain("New York");
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByActiveTrueAndStartDateAfterAndAvailablePlacesGreaterThan_WhenPlacesFilterIsHigh() {
        Page<Travel> result = travelRepository.findByActiveTrueAndStartDateAfterAndAvailablePlacesGreaterThan(
                LocalDate.now(), 20, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Travel::getDestiny)
                .containsExactly("Tokyo");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void countTravelsPerYear_ShouldReturnCountGroupedByYear() {
        Travel travel2024 = new Travel();
        travel2024.setDestiny("Lisbon");
        travel2024.setStartDate(LocalDate.of(2024, 5, 1));
        travel2024.setEndDate(LocalDate.of(2024, 5, 8));
        travel2024.setActive(true);
        travel2024.setAvailablePlaces(10);
        travel2024.setHotel(hotel);
        entityManager.persist(travel2024);

        Travel another2025 = new Travel();
        another2025.setDestiny("Dublin");
        another2025.setStartDate(LocalDate.of(2025, 9, 1));
        another2025.setEndDate(LocalDate.of(2025, 9, 8));
        another2025.setActive(true);
        another2025.setAvailablePlaces(12);
        another2025.setHotel(hotel);
        entityManager.persist(another2025);

        // inactive travel should not be counted
        Travel inactive2025 = new Travel();
        inactive2025.setDestiny("Moscow");
        inactive2025.setStartDate(LocalDate.of(2025, 11, 1));
        inactive2025.setEndDate(LocalDate.of(2025, 11, 8));
        inactive2025.setActive(false);
        inactive2025.setAvailablePlaces(5);
        inactive2025.setHotel(hotel);
        entityManager.persist(inactive2025);

        entityManager.flush();
        entityManager.clear();

        List<Object[]> result = travelRepository.countTravelsPerYear();

        Map<Integer, Long> countsByYear = result.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).longValue()
                ));

        assertThat(result).hasSize(3);
        assertThat(countsByYear.get(2026)).isEqualTo(5); // activeTravel + future, sale, noPlaces, past -> all active in 2026
        assertThat(countsByYear.get(2025)).isEqualTo(1); // another2025(Dublin)
        assertThat(countsByYear.get(2024)).isEqualTo(1); // travel2024(Lisbon)

        assertThat(result).extracting(row -> ((Number) row[0]).intValue())
                .containsExactly(2026, 2025, 2024);
    }

    @Test
    void countTravelsPerYear_WhenNoTravels_ShouldReturnEmpty() {
        travelRepository.deleteAll();
        entityManager.flush();

        List<Object[]> result = travelRepository.countTravelsPerYear();

        assertThat(result).isEmpty();
    }
}
