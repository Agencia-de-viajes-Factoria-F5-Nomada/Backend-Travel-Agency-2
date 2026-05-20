package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import com.inditex.g1_agencia_viajes.exception.BusFullException;
import com.inditex.g1_agencia_viajes.exception.DuplicateLicensePlateException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.BusMapper;
import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private BusMapper busMapper;

    private BusService busService;

    private Bus bus;
    private BusRequestDTO requestDTO;
    private BusResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        busService = new BusService(busRepository, busMapper);

        bus = new Bus();
        bus.setId(1L);
        bus.setLicensePlate("ABC-1234");
        bus.setCapacity(50);
        bus.setAvailablePlaces(50);
        bus.setBath(true);
        bus.setWifi(true);
        bus.setAC(true);
        bus.setUSB(true);

        requestDTO = new BusRequestDTO();
        requestDTO.setLicensePlate("ABC-1234");
        requestDTO.setCapacity(50);
        requestDTO.setAvailablePlaces(50);
        requestDTO.setBath(true);
        requestDTO.setWifi(true);
        requestDTO.setAC(true);
        requestDTO.setUSB(true);

        responseDTO = new BusResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setLicensePlate("ABC-1234");
        responseDTO.setCapacity(50);
        responseDTO.setAvailablePlaces(50);
        responseDTO.setBath(true);
        responseDTO.setWifi(true);
        responseDTO.setAC(true);
        responseDTO.setUSB(true);
    }

    @Test
    void getAll_ShouldReturnListOfBuses() {
        when(busRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(bus)));
        when(busMapper.toDTO(bus)).thenReturn(responseDTO);

        Page<BusResponseDTO> result = busService.getAll(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLicensePlate()).isEqualTo("ABC-1234");
    }

    @Test
    void getById_ShouldReturnBus() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busMapper.toDTO(bus)).thenReturn(responseDTO);

        BusResponseDTO result = busService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_ShouldCreateBus() {
        when(busRepository.existsByLicensePlate("ABC-1234")).thenReturn(false);
        when(busMapper.toEntity(requestDTO)).thenReturn(bus);
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(busMapper.toDTO(bus)).thenReturn(responseDTO);

        BusResponseDTO result = busService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getLicensePlate()).isEqualTo("ABC-1234");
    }

    @Test
    void create_ShouldSetAvailablePlacesToCapacityWhenNull() {
        BusRequestDTO dtoWithoutPlaces = new BusRequestDTO();
        dtoWithoutPlaces.setLicensePlate("XYZ-5678");
        dtoWithoutPlaces.setCapacity(40);

        Bus busWithoutPlaces = new Bus();
        busWithoutPlaces.setLicensePlate("XYZ-5678");
        busWithoutPlaces.setCapacity(40);
        busWithoutPlaces.setAvailablePlaces(null);

        Bus savedBus = new Bus();
        savedBus.setId(2L);
        savedBus.setLicensePlate("XYZ-5678");
        savedBus.setCapacity(40);
        savedBus.setAvailablePlaces(40);

        BusResponseDTO savedResponse = new BusResponseDTO();
        savedResponse.setId(2L);
        savedResponse.setLicensePlate("XYZ-5678");
        savedResponse.setCapacity(40);
        savedResponse.setAvailablePlaces(40);

        when(busRepository.existsByLicensePlate("XYZ-5678")).thenReturn(false);
        when(busMapper.toEntity(dtoWithoutPlaces)).thenReturn(busWithoutPlaces);
        when(busRepository.save(any(Bus.class))).thenReturn(savedBus);
        when(busMapper.toDTO(savedBus)).thenReturn(savedResponse);

        BusResponseDTO result = busService.create(dtoWithoutPlaces);

        assertThat(result.getAvailablePlaces()).isEqualTo(40);
    }

    @Test
    void create_ShouldThrowWhenLicensePlateExists() {
        when(busRepository.existsByLicensePlate("ABC-1234")).thenReturn(true);

        assertThatThrownBy(() -> busService.create(requestDTO))
                .isInstanceOf(DuplicateLicensePlateException.class)
                .hasMessageContaining("Ya existe un autobús");
    }

    @Test
    void update_ShouldUpdateBus() {
        BusRequestDTO updateDTO = new BusRequestDTO();
        updateDTO.setLicensePlate("XYZ-5678");
        updateDTO.setCapacity(40);
        updateDTO.setBath(false);
        updateDTO.setWifi(false);
        updateDTO.setAC(true);
        updateDTO.setUSB(false);

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.existsByLicensePlate("XYZ-5678")).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(busMapper.toDTO(bus)).thenReturn(responseDTO);

        BusResponseDTO result = busService.update(1L, updateDTO);

        assertThat(result).isNotNull();
    }

    @Test
    void update_ShouldThrowWhenLicensePlateExists() {
        BusRequestDTO updateDTO = new BusRequestDTO();
        updateDTO.setLicensePlate("XYZ-5678");
        updateDTO.setCapacity(40);
        updateDTO.setBath(false);
        updateDTO.setWifi(false);
        updateDTO.setAC(true);
        updateDTO.setUSB(false);

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.existsByLicensePlate("XYZ-5678")).thenReturn(true);

        assertThatThrownBy(() -> busService.update(1L, updateDTO))
                .isInstanceOf(DuplicateLicensePlateException.class);
    }

    @Test
    void update_ShouldAllowSameLicensePlate() {
        BusRequestDTO updateDTO = new BusRequestDTO();
        updateDTO.setLicensePlate("ABC-1234");
        updateDTO.setCapacity(40);
        updateDTO.setBath(false);
        updateDTO.setWifi(false);
        updateDTO.setAC(true);
        updateDTO.setUSB(false);

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.save(any(Bus.class))).thenReturn(bus);
        when(busMapper.toDTO(bus)).thenReturn(responseDTO);

        BusResponseDTO result = busService.update(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(busRepository, never()).existsByLicensePlate(anyString());
    }

    @Test
    void update_ShouldThrowResourceNotFoundException() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busService.update(99L, new BusRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_ShouldDeactivateBus() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        busService.delete(1L);

        assertThat(bus.getActive()).isFalse();
        verify(busRepository).save(bus);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAvailable_ShouldReturnBusesWithAvailablePlaces() {
        when(busRepository.findByAvailablePlacesGreaterThan(0, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(bus)));
        when(busMapper.toDTO(bus)).thenReturn(responseDTO);

        Page<BusResponseDTO> result = busService.getAvailable(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void reduceCapacity_ShouldReduceAvailablePlaces() {
        when(busRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(bus));

        busService.reduceCapacity(1L, 3);

        assertThat(bus.getAvailablePlaces()).isEqualTo(47);
        verify(busRepository).save(bus);
    }

    @Test
    void reduceCapacity_ShouldThrowBusFullException() {
        bus.setAvailablePlaces(2);
        when(busRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(bus));

        assertThatThrownBy(() -> busService.reduceCapacity(1L, 5))
                .isInstanceOf(BusFullException.class);
    }

    @Test
    void reduceCapacity_ShouldThrowResourceNotFoundException() {
        when(busRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busService.reduceCapacity(99L, 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void releaseCapacity_ShouldIncreaseAvailablePlaces() {
        bus.setAvailablePlaces(40);
        when(busRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(bus));

        busService.releaseCapacity(1L, 5);

        assertThat(bus.getAvailablePlaces()).isEqualTo(45);
        verify(busRepository).save(bus);
    }

    @Test
    void releaseCapacity_ShouldThrowResourceNotFoundException() {
        when(busRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busService.releaseCapacity(99L, 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
