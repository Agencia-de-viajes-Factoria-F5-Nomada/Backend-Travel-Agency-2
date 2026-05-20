package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BusTravelSummaryDTO;
import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.model.TripSegment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BusMapper {

    Bus toEntity(BusRequestDTO dto);

    @Mapping(target = "travels", source = "tripSegments")
    BusResponseDTO toDTO(Bus bus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(BusRequestDTO dto, @MappingTarget Bus bus);

    default List<BusTravelSummaryDTO> mapTripSegmentsToTravels(List<TripSegment> segments) {
        if (segments == null) {
            return List.of();
        }
        Set<Long> seen = new HashSet<>();
        return segments.stream()
                .map(TripSegment::getTravel)
                .filter(Objects::nonNull)
                .filter(travel -> seen.add(travel.getId()))
                .map(travel -> {
                    BusTravelSummaryDTO dto = new BusTravelSummaryDTO();
                    dto.setTravelId(travel.getId());
                    dto.setDestiny(travel.getDestiny());
                    dto.setStartDate(travel.getStartDate());
                    dto.setEndDate(travel.getEndDate());
                    return dto;
                })
                .toList();
    }
}
