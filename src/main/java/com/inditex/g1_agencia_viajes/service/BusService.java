package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import com.inditex.g1_agencia_viajes.exception.BusFullException;
import com.inditex.g1_agencia_viajes.exception.DuplicateLicensePlateException;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.BusMapper;
import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;

    @Transactional(readOnly = true)
    public Page<BusResponseDTO> getAll(Pageable pageable) {
        return busRepository.findAll(pageable).map(busMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public BusResponseDTO getById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el bus", id));
        return busMapper.toDTO(bus);
    }

    @Transactional
    public BusResponseDTO create(BusRequestDTO dto) {
        if (busRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new DuplicateLicensePlateException(dto.getLicensePlate());
        }
        Bus bus = busMapper.toEntity(dto);
        if (bus.getAvailablePlaces() == null) {
            bus.setAvailablePlaces(bus.getCapacity());
        }
        return busMapper.toDTO(busRepository.save(bus));
    }

    @Transactional
    public BusResponseDTO update(Long id, BusRequestDTO dto) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el bus", id));

        if (!bus.getLicensePlate().equals(dto.getLicensePlate())
                && busRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new DuplicateLicensePlateException(dto.getLicensePlate());
        }

        busMapper.updateFromDto(dto, bus);
        return busMapper.toDTO(busRepository.save(bus));
    }

    @Transactional
    public void delete(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el bus", id));
        bus.setActive(false);
        busRepository.save(bus);
    }

    @Transactional(readOnly = true)
    public Page<BusResponseDTO> getAvailable(Pageable pageable) {
        return busRepository.findByAvailablePlacesGreaterThan(0, pageable)
                .map(busMapper::toDTO);
    }

    @Transactional
    public void reduceCapacity(Long id, Integer plazas) {
        Bus bus = busRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("el bus", id));
        if (bus.getAvailablePlaces() == null || bus.getAvailablePlaces() < plazas) {
            throw new BusFullException(bus.getId(), bus.getLicensePlate());
        }
        bus.setAvailablePlaces(bus.getAvailablePlaces() - plazas);
        busRepository.save(bus);
    }

    @Transactional
    public void releaseCapacity(Long id, Integer plazas) {
        Bus bus = busRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("el bus", id));
        if (bus.getAvailablePlaces() == null) {
            bus.setAvailablePlaces(plazas);
        } else {
            bus.setAvailablePlaces(bus.getAvailablePlaces() + plazas);
        }
        busRepository.save(bus);
    }
}
