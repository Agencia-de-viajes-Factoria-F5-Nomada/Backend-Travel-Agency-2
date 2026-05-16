package com.inditex.g1_agencia_viajes.service;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusService {

    Page<BusResponseDTO> getAll(Pageable pageable);
    BusResponseDTO getById(Long id);
    BusResponseDTO create(BusRequestDTO dto);
    BusResponseDTO update(Long id, BusRequestDTO dto);
    void delete(Long id);
}