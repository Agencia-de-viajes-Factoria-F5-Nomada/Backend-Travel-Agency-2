package com.inditex.g1_agencia_viajes.repository;

import com.inditex.g1_agencia_viajes.model.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Hotel madridHotel;
    private Hotel barcelonaHotel;
    private Hotel parisHotel;

    @BeforeEach
    void setUp() {
        madridHotel = new Hotel();
        madridHotel.setName("Hotel Madrid Centro");
        madridHotel.setAddress("Calle Mayor 1");
        madridHotel.setCity("Madrid");
        madridHotel.setCountry("Spain");
        madridHotel.setStars(4);
        madridHotel.setCapacity(100);
        madridHotel.setAvailablePlaces(50);
        madridHotel.setActive(true);
        entityManager.persist(madridHotel);

        barcelonaHotel = new Hotel();
        barcelonaHotel.setName("Hotel Barcelona Mar");
        barcelonaHotel.setAddress("Rambla 10");
        barcelonaHotel.setCity("Barcelona");
        barcelonaHotel.setCountry("Spain");
        barcelonaHotel.setStars(5);
        barcelonaHotel.setCapacity(200);
        barcelonaHotel.setAvailablePlaces(100);
        barcelonaHotel.setActive(true);
        entityManager.persist(barcelonaHotel);

        parisHotel = new Hotel();
        parisHotel.setName("Hotel Paris Louvre");
        parisHotel.setAddress("Rue de Rivoli 5");
        parisHotel.setCity("Paris");
        parisHotel.setCountry("France");
        parisHotel.setStars(3);
        parisHotel.setCapacity(80);
        parisHotel.setAvailablePlaces(20);
        parisHotel.setActive(true);
        entityManager.persist(parisHotel);

        entityManager.flush();
    }

    @Test
    void findByCity_ShouldReturnHotelsInCity() {
        List<Hotel> result = hotelRepository.findByCity("Madrid");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Hotel Madrid Centro");
    }

    @Test
    void findByCity_WhenNoHotels_ShouldReturnEmpty() {
        List<Hotel> result = hotelRepository.findByCity("Rome");

        assertThat(result).isEmpty();
    }

    @Test
    void findByCountry_ShouldReturnHotelsInCountry() {
        List<Hotel> result = hotelRepository.findByCountry("Spain");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Hotel::getName)
                .containsExactlyInAnyOrder("Hotel Madrid Centro", "Hotel Barcelona Mar");
    }

    @Test
    void findByCountry_WhenNoHotels_ShouldReturnEmpty() {
        List<Hotel> result = hotelRepository.findByCountry("Italy");

        assertThat(result).isEmpty();
    }

    @Test
    void findByStars_ShouldReturnHotelsWithStarRating() {
        List<Hotel> result = hotelRepository.findByStars(4);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Hotel Madrid Centro");
    }

    @Test
    void findByStars_WhenNoneMatch_ShouldReturnEmpty() {
        List<Hotel> result = hotelRepository.findByStars(2);

        assertThat(result).isEmpty();
    }

    @Test
    void findByAvailablePlacesGreaterThan_ShouldReturnHotelsWithSufficientPlaces() {
        List<Hotel> result = hotelRepository.findByAvailablePlacesGreaterThan(30);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Hotel::getName)
                .containsExactlyInAnyOrder("Hotel Madrid Centro", "Hotel Barcelona Mar");
    }

    @Test
    void findByAvailablePlacesGreaterThan_WithHighThreshold_ShouldReturnEmpty() {
        List<Hotel> result = hotelRepository.findByAvailablePlacesGreaterThan(200);

        assertThat(result).isEmpty();
    }

    @Test
    void findByActive_ShouldReturnActiveHotels() {
        Hotel inactiveHotel = new Hotel();
        inactiveHotel.setName("Inactive Hotel");
        inactiveHotel.setAddress("Nowhere");
        inactiveHotel.setCity("Nowhere");
        inactiveHotel.setCountry("Nowhere");
        inactiveHotel.setStars(1);
        inactiveHotel.setActive(false);
        entityManager.persist(inactiveHotel);
        entityManager.flush();

        List<Hotel> result = hotelRepository.findByActive(true);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Hotel::getName)
                .doesNotContain("Inactive Hotel");
    }

    @Test
    void findByActive_WithPageable_ShouldReturnPagedActiveHotels() {
        Page<Hotel> result = hotelRepository.findByActive(true, PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByAvailablePlacesGreaterThan_WithPageable_ShouldReturnPagedResults() {
        Page<Hotel> result = hotelRepository.findByAvailablePlacesGreaterThan(
                0, PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}
