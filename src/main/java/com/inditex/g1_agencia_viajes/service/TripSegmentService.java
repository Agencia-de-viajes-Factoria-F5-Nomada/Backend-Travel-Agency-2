package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.TripSegmentRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TripSegmentResponseDTO;
import com.inditex.g1_agencia_viajes.exception.DriverOverlapException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripSegmentService {

    private final TripSegmentRepository tripSegmentRepository;
    private final TravelRepository travelRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final TripSegmentMapper tripSegmentMapper;

    @Transactional(readOnly = true)
    public Page<TripSegmentResponseDTO> getAll(Pageable pageable) {
        return tripSegmentRepository.findAll(pageable)
                .map(tripSegmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public TripSegmentResponseDTO getById(Long id) {
        TripSegment segment = tripSegmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el trayecto", id));
        return tripSegmentMapper.toDTO(segment);
    }

    @Transactional
    public TripSegmentResponseDTO create(TripSegmentRequestDTO dto) {
        Travel travel = travelRepository.findById(dto.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", dto.getTravelId()));
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("el bus", dto.getBusId()));
        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("el conductor", dto.getDriverId()));

        List<TripSegment> overlapping = tripSegmentRepository.findOverlappingByDriver(driver, dto.getStartTime(), dto.getEndTime());
        if (!overlapping.isEmpty()) {
            throw new DriverOverlapException(
                    dto.getDriverId(),
                    dto.getStartTime().toString(),
                    dto.getEndTime().toString()
            );
        }

        TripSegment segment = tripSegmentMapper.toEntity(dto, travel, bus, driver);
        return tripSegmentMapper.toDTO(tripSegmentRepository.save(segment));
    }

    @Transactional
    public TripSegmentResponseDTO update(Long id, TripSegmentRequestDTO dto) {
        TripSegment segment = tripSegmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el trayecto", id));

        Travel travel = travelRepository.findById(dto.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException("el viaje", dto.getTravelId()));
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("el bus", dto.getBusId()));
        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("el conductor", dto.getDriverId()));

        List<TripSegment> overlapping = tripSegmentRepository.findOverlappingByDriver(driver, dto.getStartTime(), dto.getEndTime());
        overlapping = overlapping.stream()
                .filter(s -> !Objects.equals(s.getId(), id))
                .toList();
        if (!overlapping.isEmpty()) {
            throw new DriverOverlapException(
                    dto.getDriverId(),
                    dto.getStartTime().toString(),
                    dto.getEndTime().toString()
            );
        }

        segment.setTravel(travel);
        segment.setOrigin(dto.getOrigin());
        segment.setDestination(dto.getDestination());
        segment.setStartTime(dto.getStartTime());
        segment.setEndTime(dto.getEndTime());
        segment.setBus(bus);
        segment.setDriver(driver);
        return tripSegmentMapper.toDTO(tripSegmentRepository.save(segment));
    }

    @Transactional
    public void delete(Long id) {
        if (!tripSegmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("el trayecto", id);
        }
        tripSegmentRepository.deleteById(id);
    }
}
