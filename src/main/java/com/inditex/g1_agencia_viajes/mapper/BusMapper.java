package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import com.inditex.g1_agencia_viajes.model.Bus;
import org.springframework.stereotype.Component;

@Component
public class BusMapper {

    public Bus toEntity(BusRequestDTO dto) {
        Bus bus = new Bus();
        bus.setLicensePlate(dto.getLicensePlate());
        bus.setCapacity(dto.getCapacity());
        bus.setBath(dto.getBath());
        bus.setWifi(dto.getWifi());
        bus.setAC(dto.getAC());
        bus.setUSB(dto.getUSB());
        if (dto.getActive() != null) {
            bus.setActive(dto.getActive());
        }
        return bus;
    }

    public BusResponseDTO toDTO(Bus bus) {
        BusResponseDTO dto = new BusResponseDTO();
        dto.setId(bus.getId());
        dto.setLicensePlate(bus.getLicensePlate());
        dto.setCapacity(bus.getCapacity());
        dto.setBath(bus.getBath());
        dto.setWifi(bus.getWifi());
        dto.setAC(bus.getAC());
        dto.setUSB(bus.getUSB());
        dto.setActive(bus.getActive());
        return dto;
    }
}
