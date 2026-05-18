package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.HotelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.HotelResponseDTO;
import com.inditex.g1_agencia_viajes.exception.HotelNotAvailableException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.HotelMapper;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.repository.HotelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    public HotelService(HotelRepository hotelRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
    }

    @Transactional
    public HotelResponseDTO create(HotelRequestDTO dto) {
        Hotel hotel = hotelMapper.toEntity(dto);
        return hotelMapper.toDTO(hotelRepository.save(hotel));
    }

    @Transactional(readOnly = true)
    public Page<HotelResponseDTO> getAll(Pageable pageable) {
        return hotelRepository.findAll(pageable)
                .map(hotelMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public HotelResponseDTO getById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el hotel", id));
        return hotelMapper.toDTO(hotel);
    }

    @Transactional(readOnly = true)
    public Page<HotelResponseDTO> getActive(Pageable pageable) {
        return hotelRepository.findByActive(true, pageable)
                .map(hotelMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<HotelResponseDTO> getAvailable(Pageable pageable) {
        return hotelRepository.findByAvailablePlacesGreaterThan(0, pageable)
                .map(hotelMapper::toDTO);
    }

    @Transactional
    public HotelResponseDTO update(Long id, HotelRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el hotel", id));
        if (dto.getName() != null)            hotel.setName(dto.getName());
        if (dto.getAddress() != null)         hotel.setAddress(dto.getAddress());
        if (dto.getCity() != null)            hotel.setCity(dto.getCity());
        if (dto.getCountry() != null)         hotel.setCountry(dto.getCountry());
        if (dto.getStars() != null)           hotel.setStars(dto.getStars());
        if (dto.getCapacity() != null)        hotel.setCapacity(dto.getCapacity());
        if (dto.getAvailablePlaces() != null) hotel.setAvailablePlaces(dto.getAvailablePlaces());
        if (dto.getHalfBoardPrice() != null)  hotel.setHalfBoardPrice(dto.getHalfBoardPrice());
        if (dto.getFullBoardPrice() != null)  hotel.setFullBoardPrice(dto.getFullBoardPrice());
        if (dto.getImageUrl() != null)        hotel.setImageUrl(dto.getImageUrl());
        if (dto.getActive() != null)          hotel.setActive(dto.getActive());
        return hotelMapper.toDTO(hotelRepository.save(hotel));
    }

    @Transactional
    public void delete(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el hotel", id));
        hotel.setActive(false);
        hotelRepository.save(hotel);
    }

    @Transactional
    public void reduceCapacity(Long id, Integer plazas) {
        Hotel hotel = hotelRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("el hotel", id));
        if (hotel.getAvailablePlaces() < plazas) {
            throw new HotelNotAvailableException(id);
        }
        hotel.setAvailablePlaces(hotel.getAvailablePlaces() - plazas);
        hotelRepository.save(hotel);
    }

    @Transactional
    public void releaseCapacity(Long id, Integer plazas) {
        Hotel hotel = hotelRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("el hotel", id));
        hotel.setAvailablePlaces(hotel.getAvailablePlaces() + plazas);
        hotelRepository.save(hotel);
    }
}