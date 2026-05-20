package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.TripSegmentRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TripSegmentResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.TripSegmentMapper;
import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.model.Driver;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.TripSegment;
import com.inditex.g1_agencia_viajes.repository.BusRepository;
import com.inditex.g1_agencia_viajes.repository.DriverRepository;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import com.inditex.g1_agencia_viajes.repository.TripSegmentRepository;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripSegmentServiceTest {

    @Mock
    private TripSegmentRepository tripSegmentRepository;

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private DriverRepository driverRepository;

    private TripSegmentMapper tripSegmentMapper;

    private TripSegmentService tripSegmentService;

    private TripSegment tripSegment;
    private Travel travel;
    private Bus bus;
    private Driver driver;

    @BeforeEach
    void setUp() {
        tripSegmentMapper = Mappers.getMapper(TripSegmentMapper.class);
        tripSegmentService = new TripSegmentService(tripSegmentRepository, travelRepository,
                busRepository, driverRepository, tripSegmentMapper);

        travel = new Travel();
        travel.setId(1L);

        bus = new Bus();
        bus.setId(1L);
        bus.setLicensePlate("ABC-123");

        driver = new Driver();
        driver.setId(1L);
        driver.setName("Driver Test");

        tripSegment = new TripSegment();
        tripSegment.setId(1L);
        tripSegment.setTravel(travel);
        tripSegment.setOrigin("Madrid");
        tripSegment.setDestination("Barcelona");
        tripSegment.setStartTime(LocalDateTime.of(2026, 6, 15, 8, 0));
        tripSegment.setEndTime(LocalDateTime.of(2026, 6, 15, 12, 0));
        tripSegment.setBus(bus);
        tripSegment.setDriver(driver);
    }

    @Test
    void getAll_ShouldReturnAllSegments() {
        when(tripSegmentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(tripSegment)));

        Page<TripSegmentResponseDTO> result = tripSegmentService.getAll(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getOrigin()).isEqualTo("Madrid");
    }

    @Test
    void getById_ShouldReturnSegment() {
        when(tripSegmentRepository.findById(1L)).thenReturn(Optional.of(tripSegment));

        TripSegmentResponseDTO result = tripSegmentService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException() {
        when(tripSegmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripSegmentService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_ShouldCreateSegment() {
        TripSegmentRequestDTO dto = new TripSegmentRequestDTO();
        dto.setTravelId(1L);
        dto.setOrigin("Madrid");
        dto.setDestination("Barcelona");
        dto.setStartTime(LocalDateTime.of(2026, 6, 15, 8, 0));
        dto.setEndTime(LocalDateTime.of(2026, 6, 15, 12, 0));
        dto.setBusId(1L);
        dto.setDriverId(1L);

        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(tripSegmentRepository.findOverlappingByDriver(any(), any(), any())).thenReturn(List.of());
        when(tripSegmentRepository.save(any(TripSegment.class))).thenReturn(tripSegment);

        TripSegmentResponseDTO result = tripSegmentService.create(dto);

        assertThat(result).isNotNull();
        verify(travelRepository).findById(1L);
        verify(busRepository).findById(1L);
        verify(driverRepository).findById(1L);
    }

    @Test
    void update_ShouldUpdateSegment() {
        TripSegmentRequestDTO dto = new TripSegmentRequestDTO();
        dto.setTravelId(1L);
        dto.setOrigin("Barcelona");
        dto.setDestination("Madrid");
        dto.setStartTime(LocalDateTime.of(2026, 6, 16, 8, 0));
        dto.setEndTime(LocalDateTime.of(2026, 6, 16, 12, 0));
        dto.setBusId(1L);
        dto.setDriverId(1L);

        when(tripSegmentRepository.findById(1L)).thenReturn(Optional.of(tripSegment));
        when(travelRepository.findById(1L)).thenReturn(Optional.of(travel));
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(tripSegmentRepository.findOverlappingByDriver(any(), any(), any())).thenReturn(List.of());
        when(tripSegmentRepository.save(any(TripSegment.class))).thenReturn(tripSegment);

        TripSegmentResponseDTO result = tripSegmentService.update(1L, dto);

        assertThat(result).isNotNull();
    }

    @Test
    void update_ShouldThrowResourceNotFoundException() {
        when(tripSegmentRepository.findById(99L)).thenReturn(Optional.empty());

        TripSegmentRequestDTO dto = new TripSegmentRequestDTO();
        assertThatThrownBy(() -> tripSegmentService.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_ShouldDeleteSegment() {
        when(tripSegmentRepository.existsById(1L)).thenReturn(true);

        tripSegmentService.delete(1L);

        verify(tripSegmentRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException() {
        when(tripSegmentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> tripSegmentService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
