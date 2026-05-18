package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.DriverRequestDTO;
import com.inditex.g1_agencia_viajes.dto.DriverResponseDTO;
import com.inditex.g1_agencia_viajes.exception.ResourceNotFoundException;
import com.inditex.g1_agencia_viajes.mapper.DriverMapper;
import com.inditex.g1_agencia_viajes.model.Driver;
import com.inditex.g1_agencia_viajes.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Transactional
    public DriverResponseDTO create(DriverRequestDTO dto) {
        Driver driver = driverMapper.toEntity(dto);
        return driverMapper.toDTO(driverRepository.save(driver));
    }

    @Transactional(readOnly = true)
    public Page<DriverResponseDTO> getAll(Pageable pageable) {
        return driverRepository.findAll(pageable)
                .map(driverMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public DriverResponseDTO getById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el conductor", id));
        return driverMapper.toDTO(driver);
    }

    @Transactional(readOnly = true)
    public Page<DriverResponseDTO> getActive(Pageable pageable) {
        return driverRepository.findByLicenceActive(true, pageable)
                .map(driverMapper::toDTO);
    }

    @Transactional
    public DriverResponseDTO update(Long id, DriverRequestDTO dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el conductor", id));
        if (dto.getName() != null)          driver.setName(dto.getName());
        if (dto.getPhone() != null)         driver.setPhone(dto.getPhone());
        if (dto.getImageUrl() != null)      driver.setImageUrl(dto.getImageUrl());
        if (dto.getLicenceActive() != null) driver.setLicenceActive(dto.getLicenceActive());
        if (dto.getActive() != null)        driver.setActive(dto.getActive());
        return driverMapper.toDTO(driverRepository.save(driver));
    }

    @Transactional
    public void delete(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("el conductor", id));
        driver.setActive(false);
        driverRepository.save(driver);
    }
}