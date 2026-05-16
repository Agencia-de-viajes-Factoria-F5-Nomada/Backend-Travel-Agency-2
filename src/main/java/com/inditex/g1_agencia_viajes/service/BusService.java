package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
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
        return busMapper.toDTO(busRepository.save(busMapper.toEntity(dto)));
    }

    @Transactional
    public BusResponseDTO update(Long id, BusRequestDTO dto) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el bus", id));
        bus.setLicensePlate(dto.getLicensePlate());
        bus.setCapacity(dto.getCapacity());
        bus.setBath(dto.getBath());
        bus.setWifi(dto.getWifi());
        bus.setAC(dto.getAC());
        bus.setUSB(dto.getUSB());
        return busMapper.toDTO(busRepository.save(bus));
    }

    @Transactional
    public void delete(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el bus", id));
        bus.setActive(false);
        busRepository.save(bus);
    }
}
