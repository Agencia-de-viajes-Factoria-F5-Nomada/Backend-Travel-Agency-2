package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.TravelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TravelResponseDTO;
import com.inditex.g1_agencia_viajes.mapper.TravelMapper;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.repository.HotelRepository;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.repository.TravelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
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
    public List<TravelResponseDTO> getAll() {
        List<Travel> travels = travelRepository.findAll();
        Collections.shuffle(travels);
        return travels.stream()
                .map(travelMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TravelResponseDTO> getAvailable() {
        List<Travel> travels = travelRepository.findAll()
                .stream()
                .filter(t -> t.getAvailablePlaces() != null && t.getAvailablePlaces() > 0)
                .filter(t -> t.getStartDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
        Collections.shuffle(travels);
        return travels.stream()
                .map(travelMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TravelResponseDTO> getOnSale() {
        List<Travel> travels = travelRepository.findAll()
                .stream()
                .filter(t -> Boolean.TRUE.equals(t.getSale()))
                .collect(Collectors.toList());
        Collections.shuffle(travels);
        return travels.stream()
                .map(travelMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TravelResponseDTO getById(Long id) {
        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", id));
        return travelMapper.toDTO(travel);
    }

    @Transactional
    public TravelResponseDTO create(TravelRequestDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate()) ||
                dto.getEndDate().isEqual(dto.getStartDate())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("el hotel", dto.getHotelId()));
        Travel travel = travelMapper.toEntity(dto, hotel);
        return travelMapper.toDTO(travelRepository.save(travel));
    }

    @Transactional
    public TravelResponseDTO update(Long id, TravelRequestDTO dto) {
        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", id));
        if (dto.getEndDate().isBefore(dto.getStartDate()) ||
                dto.getEndDate().isEqual(dto.getStartDate())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio");
        }
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