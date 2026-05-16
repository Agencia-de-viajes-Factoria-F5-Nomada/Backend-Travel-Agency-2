package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.TravelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TravelResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.TravelMapper;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.Offer;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.repository.HotelRepository;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelServiceTest {

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private TravelMapper travelMapper;

    private TravelService travelService;

    private Travel travel;
    private Travel futureTravel;
    private Travel saleTravel;
    private Hotel hotel;
    private TravelRequestDTO requestDTO;
    private TravelResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        travelService = new TravelService(travelRepository, hotelRepository, travelMapper);

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Hotel Test");
        hotel.setCity("Madrid");
        hotel.setCountry("España");

        travel = new Travel();
        travel.setId(1L);
        travel.setDestiny("París");
        travel.setStartDate(LocalDate.of(2025, 6, 15));
        travel.setEndDate(LocalDate.of(2026, 6, 22));
        travel.setSale(false);
        travel.setAvailablePlaces(20);
        travel.setActive(true);
        travel.setHotel(hotel);

        futureTravel = new Travel();
        futureTravel.setId(2L);
        futureTravel.setDestiny("Londres");
        futureTravel.setStartDate(LocalDate.now().plusDays(30));
        futureTravel.setEndDate(LocalDate.now().plusDays(37));
        futureTravel.setSale(false);
        futureTravel.setAvailablePlaces(15);
        futureTravel.setActive(true);
        futureTravel.setHotel(hotel);

        saleTravel = new Travel();
        saleTravel.setId(3L);
        saleTravel.setDestiny("Roma");
        saleTravel.setStartDate(LocalDate.now().plusDays(10));
        saleTravel.setEndDate(LocalDate.now().plusDays(17));
        saleTravel.setSale(true);
        saleTravel.setAvailablePlaces(0);
        saleTravel.setActive(true);
        saleTravel.setHotel(hotel);
        saleTravel.setOffer(new Offer());

        requestDTO = new TravelRequestDTO();
        requestDTO.setDestiny("París");
        requestDTO.setStartDate(LocalDate.of(2026, 7, 1));
        requestDTO.setEndDate(LocalDate.of(2026, 7, 10));
        requestDTO.setSale(false);
        requestDTO.setAvailablePlaces(20);
        requestDTO.setHotelId(1L);

        responseDTO = new TravelResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setDestiny("París");
    }

    @Test
    void getAll_ShouldReturnAllTravels() {
        when(travelRepository.findByActiveTrue()).thenReturn(List.of(travel));
        when(travelMapper.toDTO(travel)).thenReturn(responseDTO);

        List<TravelResponseDTO> result = travelService.getAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getAvailable_ShouldReturnTravelsWithAvailablePlacesAndFutureDates() {
        when(travelRepository.findByActiveTrueAndStartDateAfter(any())).thenReturn(List.of(futureTravel, saleTravel));
        when(travelMapper.toDTO(futureTravel)).thenReturn(responseDTO);

        List<TravelResponseDTO> result = travelService.getAvailable();

        assertThat(result).hasSize(1);
    }

    @Test
    void getOnSale_ShouldReturnSaleTravels() {
        when(travelRepository.findBySaleTrueAndActiveTrueAndStartDateAfter(any())).thenReturn(List.of(saleTravel));
        when(travelMapper.toDTO(saleTravel)).thenReturn(responseDTO);

        List<TravelResponseDTO> result = travelService.getOnSale();

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_ShouldReturnTravel() {
        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(travelMapper.toDTO(travel)).thenReturn(responseDTO);

        TravelResponseDTO result = travelService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException() {
        when(travelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> travelService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hemos podido encontrar la información de el viaje, con el id: 99");
    }

    @Test
    void create_ShouldCreateTravel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(travelMapper.toEntity(requestDTO, hotel)).thenReturn(travel);
        when(travelRepository.save(travel)).thenReturn(travel);
        when(travelMapper.toDTO(travel)).thenReturn(responseDTO);

        TravelResponseDTO result = travelService.create(requestDTO);

        assertThat(result).isNotNull();
    }

    @Test
    void create_ShouldThrowWhenEndDateBeforeStartDate() {
        requestDTO.setStartDate(LocalDate.of(2026, 7, 10));
        requestDTO.setEndDate(LocalDate.of(2026, 7, 1));

        assertThatThrownBy(() -> travelService.create(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha de fin debe ser posterior");
    }

    @Test
    void create_ShouldThrowWhenEndDateEqualsStartDate() {
        requestDTO.setStartDate(LocalDate.of(2026, 7, 1));
        requestDTO.setEndDate(LocalDate.of(2026, 7, 1));

        assertThatThrownBy(() -> travelService.create(requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha de fin debe ser posterior");
    }

    @Test
    void create_ShouldThrowWhenHotelNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());
        requestDTO.setHotelId(99L);

        assertThatThrownBy(() -> travelService.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hemos podido encontrar la información de el hotel, con el id: 99");
    }

    @Test
    void update_ShouldUpdateTravel() {
        TravelRequestDTO updateDTO = new TravelRequestDTO();
        updateDTO.setDestiny("Londres");
        updateDTO.setStartDate(LocalDate.of(2026, 8, 1));
        updateDTO.setEndDate(LocalDate.of(2026, 8, 10));
        updateDTO.setHotelId(1L);
        updateDTO.setAvailablePlaces(30);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(travelRepository.save(any(Travel.class))).thenReturn(travel);
        when(travelMapper.toDTO(any(Travel.class))).thenReturn(responseDTO);

        TravelResponseDTO result = travelService.update(1L, updateDTO);

        assertThat(result).isNotNull();
    }

    @Test
    void update_ShouldThrowWhenEndDateBeforeStartDate() {
        TravelRequestDTO updateDTO = new TravelRequestDTO();
        updateDTO.setDestiny("Londres");
        updateDTO.setStartDate(LocalDate.of(2026, 8, 10));
        updateDTO.setEndDate(LocalDate.of(2026, 8, 1));

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        assertThatThrownBy(() -> travelService.update(1L, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha de fin debe ser posterior");
    }

    @Test
    void update_ShouldThrowWhenTravelNotFound() {
        when(travelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> travelService.update(99L, new TravelRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hemos podido encontrar la información de el viaje, con el id: 99");
    }

    @Test
    void delete_ShouldDeactivateTravel() {
        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));

        travelService.delete(1L);

        assertThat(travel.getActive()).isFalse();
        verify(travelRepository).save(travel);
    }

    @Test
    void delete_ShouldThrowWhenTravelNotFound() {
        when(travelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> travelService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No hemos podido encontrar la información de el viaje, con el id: 99");
    }
}
