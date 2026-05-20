package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.TripSegmentResponseDTO;
import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.model.Driver;
import com.inditex.g1_agencia_viajes.model.Travel;
import com.inditex.g1_agencia_viajes.model.TripSegment;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TripSegmentMapperTest {

    private final TripSegmentMapper mapper = Mappers.getMapper(TripSegmentMapper.class);

    @Test
    void toDTO_ShouldMapAllFields() {
        Travel travel = new Travel();
        travel.setId(1L);

        Bus bus = new Bus();
        bus.setId(10L);
        bus.setLicensePlate("1234-ABC");

        Driver driver = new Driver();
        driver.setId(20L);
        driver.setName("Carlos Ruiz");

        TripSegment segment = new TripSegment();
        segment.setId(1L);
        segment.setTravel(travel);
        segment.setOrigin("Madrid");
        segment.setDestination("Barcelona");
        segment.setStartTime(LocalDateTime.of(2026, 7, 1, 8, 0));
        segment.setEndTime(LocalDateTime.of(2026, 7, 1, 12, 0));
        segment.setBus(bus);
        segment.setDriver(driver);

        TripSegmentResponseDTO dto = mapper.toDTO(segment);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTravelId()).isEqualTo(1L);
        assertThat(dto.getOrigin()).isEqualTo("Madrid");
        assertThat(dto.getDestination()).isEqualTo("Barcelona");
        assertThat(dto.getStartTime()).isEqualTo(LocalDateTime.of(2026, 7, 1, 8, 0));
        assertThat(dto.getEndTime()).isEqualTo(LocalDateTime.of(2026, 7, 1, 12, 0));
        assertThat(dto.getBusId()).isEqualTo(10L);
        assertThat(dto.getBusLicensePlate()).isEqualTo("1234-ABC");
        assertThat(dto.getDriverId()).isEqualTo(20L);
        assertThat(dto.getDriverName()).isEqualTo("Carlos Ruiz");
    }

    @Test
    void toEntity_WithEntities_ShouldSetTravelBusDriver() {
        Travel travel = new Travel();
        travel.setId(1L);
        Bus bus = new Bus();
        bus.setId(10L);
        Driver driver = new Driver();
        driver.setId(20L);

        com.inditex.g1_agencia_viajes.dto.TripSegmentRequestDTO dto =
                new com.inditex.g1_agencia_viajes.dto.TripSegmentRequestDTO();
        dto.setOrigin("Madrid");
        dto.setDestination("Valencia");

        TripSegment segment = mapper.toEntity(dto, travel, bus, driver);

        assertThat(segment.getOrigin()).isEqualTo("Madrid");
        assertThat(segment.getDestination()).isEqualTo("Valencia");
        assertThat(segment.getTravel()).isSameAs(travel);
        assertThat(segment.getBus()).isSameAs(bus);
        assertThat(segment.getDriver()).isSameAs(driver);
    }
}
