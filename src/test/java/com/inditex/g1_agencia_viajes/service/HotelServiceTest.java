package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.HotelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.HotelResponseDTO;
import com.inditex.g1_agencia_viajes.exception.HotelNotAvailableException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.HotelMapper;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.repository.HotelRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    private HotelService hotelService;

    private Hotel hotel;
    private HotelRequestDTO requestDTO;
    private HotelResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        hotelService = new HotelService(hotelRepository, hotelMapper);

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Hotel Test");
        hotel.setCity("Madrid");
        hotel.setCountry("España");
        hotel.setAddress("Calle Test 123");
        hotel.setStars(4);
        hotel.setCapacity(100);
        hotel.setAvailablePlaces(50);
        hotel.setHalfBoardPrice(100.0);
        hotel.setFullBoardPrice(150.0);
        hotel.setActive(true);

        requestDTO = new HotelRequestDTO();
        requestDTO.setName("Hotel Test");
        requestDTO.setCity("Madrid");
        requestDTO.setCountry("España");
        requestDTO.setAddress("Calle Test 123");
        requestDTO.setStars(4);
        requestDTO.setCapacity(100);
        requestDTO.setAvailablePlaces(50);
        requestDTO.setHalfBoardPrice(100.0);
        requestDTO.setFullBoardPrice(150.0);
        requestDTO.setActive(true);

        responseDTO = new HotelResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Hotel Test");
        responseDTO.setCity("Madrid");
        responseDTO.setCountry("España");
        responseDTO.setAddress("Calle Test 123");
        responseDTO.setStars(4);
        responseDTO.setCapacity(100);
        responseDTO.setAvailablePlaces(50);
        responseDTO.setHalfBoardPrice(100.0);
        responseDTO.setFullBoardPrice(150.0);
        responseDTO.setActive(true);
    }

    @Test
    void create_ShouldReturnHotelResponseDTO() {
        when(hotelMapper.toEntity(requestDTO)).thenReturn(hotel);
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.toDTO(hotel)).thenReturn(responseDTO);

        HotelResponseDTO result = hotelService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Hotel Test");
        verify(hotelMapper).toEntity(requestDTO);
        verify(hotelRepository).save(hotel);
        verify(hotelMapper).toDTO(hotel);
    }

    @Test
    void getAll_ShouldReturnListOfHotels() {
        when(hotelRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(hotel)));
        when(hotelMapper.toDTO(hotel)).thenReturn(responseDTO);

        Page<HotelResponseDTO> result = hotelService.getAll(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Hotel Test");
    }

    @Test
    void getById_ShouldReturnHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelMapper.toDTO(hotel)).thenReturn(responseDTO);

        HotelResponseDTO result = hotelService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getActive_ShouldReturnActiveHotels() {
        when(hotelRepository.findByActive(eq(true), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(hotel)));
        when(hotelMapper.toDTO(hotel)).thenReturn(responseDTO);

        Page<HotelResponseDTO> result = hotelService.getActive(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAvailable_ShouldReturnHotelsWithAvailablePlaces() {
        when(hotelRepository.findByAvailablePlacesGreaterThan(eq(0), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(hotel)));
        when(hotelMapper.toDTO(hotel)).thenReturn(responseDTO);

        Page<HotelResponseDTO> result = hotelService.getAvailable(Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void update_ShouldReturnUpdatedHotel() {
        HotelRequestDTO updateDTO = new HotelRequestDTO();
        updateDTO.setName("Hotel Updated");
        updateDTO.setCity("Barcelona");

        Hotel existingHotel = new Hotel();
        existingHotel.setId(1L);
        existingHotel.setName("Hotel Test");
        existingHotel.setCity("Madrid");

        Hotel updatedHotel = new Hotel();
        updatedHotel.setId(1L);
        updatedHotel.setName("Hotel Updated");
        updatedHotel.setCity("Barcelona");

        HotelResponseDTO updatedResponse = new HotelResponseDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("Hotel Updated");
        updatedResponse.setCity("Barcelona");

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(existingHotel));
        when(hotelRepository.save(existingHotel)).thenReturn(updatedHotel);
        when(hotelMapper.toDTO(updatedHotel)).thenReturn(updatedResponse);

        HotelResponseDTO result = hotelService.update(1L, updateDTO);

        assertThat(result.getName()).isEqualTo("Hotel Updated");
        assertThat(result.getCity()).isEqualTo("Barcelona");
    }

    @Test
    void update_ShouldThrowResourceNotFoundException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.update(99L, new HotelRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_ShouldDeactivateHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        hotelService.delete(1L);

        assertThat(hotel.getActive()).isFalse();
        verify(hotelRepository).save(hotel);
    }

    @Test
    void delete_ShouldThrowResourceNotFoundException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reduceCapacity_ShouldReduceAvailablePlaces() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        hotelService.reduceCapacity(1L, 10);

        assertThat(hotel.getAvailablePlaces()).isEqualTo(40);
        verify(hotelRepository).save(hotel);
    }

    @Test
    void reduceCapacity_ShouldThrowHotelNotAvailableException() {
        hotel.setAvailablePlaces(5);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThatThrownBy(() -> hotelService.reduceCapacity(1L, 10))
                .isInstanceOf(HotelNotAvailableException.class);
    }

    @Test
    void releaseCapacity_ShouldIncreaseAvailablePlaces() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        hotelService.releaseCapacity(1L, 20);

        assertThat(hotel.getAvailablePlaces()).isEqualTo(70);
        verify(hotelRepository).save(hotel);
    }

    @Test
    void releaseCapacity_ShouldThrowResourceNotFoundException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.releaseCapacity(99L, 10))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
