package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.BusRequestDTO;
import com.inditex.g1_agencia_viajes.dto.BusResponseDTO;
import com.inditex.g1_agencia_viajes.dto.BusTravelSummaryDTO;
import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.TripSegment;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BusMapperTest {

    private final BusMapper mapper = Mappers.getMapper(BusMapper.class);

    @Test
    void toDTO_ShouldMapAllFields() {
        Bus bus = new Bus();
        bus.setId(1L);
        bus.setLicensePlate("1234-ABC");
        bus.setCapacity(50);
        bus.setLocation("Madrid, Garaje Central");
        bus.setBath(true);
        bus.setWifi(false);
        bus.setAC(true);
        bus.setUSB(true);
        bus.setActive(true);

        BusResponseDTO dto = mapper.toDTO(bus);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getLicensePlate()).isEqualTo("1234-ABC");
        assertThat(dto.getCapacity()).isEqualTo(50);
        assertThat(dto.getLocation()).isEqualTo("Madrid, Garaje Central");
        assertThat(dto.getBath()).isTrue();
        assertThat(dto.getWifi()).isFalse();
        assertThat(dto.getAC()).isTrue();
        assertThat(dto.getUSB()).isTrue();
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    void toDTO_WithTripSegments_ShouldExtractUniqueTravels() {
        Travel paris = new Travel();
        paris.setId(1L);
        paris.setDestiny("París");
        paris.setStartDate(LocalDate.of(2026, 7, 1));
        paris.setEndDate(LocalDate.of(2026, 7, 7));

        Travel london = new Travel();
        london.setId(2L);
        london.setDestiny("Londres");
        london.setStartDate(LocalDate.of(2026, 8, 1));
        london.setEndDate(LocalDate.of(2026, 8, 5));

        TripSegment seg1 = new TripSegment();
        seg1.setTravel(paris);
        TripSegment seg2 = new TripSegment();
        seg2.setTravel(paris);
        TripSegment seg3 = new TripSegment();
        seg3.setTravel(london);

        Bus bus = new Bus();
        bus.setTripSegments(List.of(seg1, seg2, seg3));

        BusResponseDTO dto = mapper.toDTO(bus);

        assertThat(dto.getTravels()).hasSize(2);
        assertThat(dto.getTravels()).extracting(BusTravelSummaryDTO::getTravelId)
                .containsExactlyInAnyOrder(1L, 2L);
        assertThat(dto.getTravels()).extracting(BusTravelSummaryDTO::getDestiny)
                .containsExactlyInAnyOrder("París", "Londres");
    }

    @Test
    void toDTO_WithNullTripSegments_ShouldReturnEmptyTravels() {
        Bus bus = new Bus();
        BusResponseDTO dto = mapper.toDTO(bus);

        assertThat(dto.getTravels()).isEmpty();
    }

    @Test
    void toDTO_WithNullTravelInSegment_ShouldSkipNullTravel() {
        TripSegment seg = new TripSegment();
        seg.setTravel(null);

        Bus bus = new Bus();
        bus.setTripSegments(List.of(seg));

        BusResponseDTO dto = mapper.toDTO(bus);

        assertThat(dto.getTravels()).isEmpty();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        BusRequestDTO dto = new BusRequestDTO();
        dto.setLicensePlate("5678-DEF");
        dto.setCapacity(40);
        dto.setLocation("Barcelona, Estación Norte");
        dto.setBath(true);
        dto.setWifi(true);
        dto.setAC(false);
        dto.setUSB(true);
        dto.setActive(true);

        Bus bus = mapper.toEntity(dto);

        assertThat(bus.getLicensePlate()).isEqualTo("5678-DEF");
        assertThat(bus.getCapacity()).isEqualTo(40);
        assertThat(bus.getLocation()).isEqualTo("Barcelona, Estación Norte");
        assertThat(bus.getBath()).isTrue();
        assertThat(bus.getWifi()).isTrue();
        assertThat(bus.getAC()).isFalse();
        assertThat(bus.getUSB()).isTrue();
        assertThat(bus.getActive()).isTrue();
    }

    @Test
    void updateFromDto_ShouldUpdateNonNullFields() {
        Bus bus = new Bus();
        bus.setLicensePlate("1234-ABC");
        bus.setCapacity(50);
        bus.setLocation("Madrid");

        BusRequestDTO dto = new BusRequestDTO();
        dto.setLocation("Barcelona");

        mapper.updateFromDto(dto, bus);

        assertThat(bus.getLicensePlate()).isEqualTo("1234-ABC");
        assertThat(bus.getCapacity()).isEqualTo(50);
        assertThat(bus.getLocation()).isEqualTo("Barcelona");
    }
}
