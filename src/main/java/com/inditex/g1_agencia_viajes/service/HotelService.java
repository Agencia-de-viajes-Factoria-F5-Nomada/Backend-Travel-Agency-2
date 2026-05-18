package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.HotelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.HotelResponseDTO;
import com.inditex.g1_agencia_viajes.exception.HotelNotAvailableException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.HotelMapper;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

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
        hotelMapper.updateFromDto(dto, hotel);
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