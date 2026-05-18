package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.TripSegmentRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TripSegmentResponseDTO;
import com.inditex.g1_agencia_viajes.model.TripSegment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TripSegmentMapper {

    default TripSegment toEntity(TripSegmentRequestDTO dto,
                                  com.inditex.g1_agencia_viajes.model.Travel travel,
                                  com.inditex.g1_agencia_viajes.model.Bus bus,
                                  com.inditex.g1_agencia_viajes.model.Driver driver) {
        TripSegment segment = toEntity(dto);
        segment.setTravel(travel);
        segment.setBus(bus);
        segment.setDriver(driver);
        return segment;
    }

    TripSegment toEntity(TripSegmentRequestDTO dto);

    @Mapping(target = "travelId", source = "travel.id")
    @Mapping(target = "busId", source = "bus.id")
    @Mapping(target = "busLicensePlate", source = "bus.licensePlate")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.name")
    TripSegmentResponseDTO toDTO(TripSegment segment);

    void updateFromDto(TripSegmentRequestDTO dto, @MappingTarget TripSegment segment);
}
