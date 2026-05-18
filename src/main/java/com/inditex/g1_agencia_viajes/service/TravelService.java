package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.TravelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TravelResponseDTO;
import com.inditex.g1_agencia_viajes.mapper.TravelMapper;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.repository.HotelRepository;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelService {

    private final TravelRepository travelRepository;
    private final HotelRepository hotelRepository;
    private final TravelMapper travelMapper;

    public TravelService(TravelRepository travelRepository,
                         HotelRepository hotelRepository,
                         TravelMapper travelMapper) {
        this.travelRepository = travelRepository;
        this.hotelRepository = hotelRepository;
        this.travelMapper = travelMapper;
    }

    @Transactional(readOnly = true)
    public Page<TravelResponseDTO> getAll(Pageable pageable) {
        return travelRepository.findByActiveTrue(pageable)
                .map(travelMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<TravelResponseDTO> getAvailable(Pageable pageable) {
        return travelRepository.findByActiveTrueAndStartDateAfterAndAvailablePlacesGreaterThan(LocalDate.now(), 0, pageable)
                .map(travelMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<TravelResponseDTO> getOnSale(Pageable pageable) {
        return travelRepository.findBySaleTrueAndActiveTrueAndStartDateAfter(LocalDate.now(), pageable)
                .map(travelMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public TravelResponseDTO getById(Long id) {
        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", id));
        return travelMapper.toDTO(travel);
    }

    @Transactional
    public TravelResponseDTO create(TravelRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("el hotel", dto.getHotelId()));
        Travel travel = travelMapper.toEntity(dto, hotel);
        return travelMapper.toDTO(travelRepository.save(travel));
    }

    @Transactional
    public TravelResponseDTO update(Long id, TravelRequestDTO dto) {
        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", id));
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("el hotel", dto.getHotelId()));
        travel.setDestiny(dto.getDestiny());
        travel.setStartDate(dto.getStartDate());
        travel.setEndDate(dto.getEndDate());
        travel.setSale(dto.getSale());
        travel.setAvailablePlaces(dto.getAvailablePlaces());
        travel.setHotel(hotel);
        return travelMapper.toDTO(travelRepository.save(travel));
    }

    @Transactional
    public void delete(Long id) {
        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", id));
        travel.setActive(false);
        travelRepository.save(travel);
    }
}