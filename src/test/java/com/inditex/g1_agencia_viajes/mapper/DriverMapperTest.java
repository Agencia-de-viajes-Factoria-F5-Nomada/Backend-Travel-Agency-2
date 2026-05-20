package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.DriverRequestDTO;
import com.inditex.g1_agencia_viajes.dto.DriverResponseDTO;
import com.inditex.g1_agencia_viajes.model.Bus;
import com.inditex.g1_agencia_viajes.model.Driver;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class DriverMapperTest {

    private final DriverMapper mapper = Mappers.getMapper(DriverMapper.class);

    @Test
    void toDTO_WithBus_ShouldMapBusFields() {
        Bus bus = new Bus();
        bus.setId(10L);
        bus.setLicensePlate("1234-ABC");

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setName("Juan");
        driver.setPhone("+34600000000");
        driver.setBus(bus);
        driver.setLicenceActive(true);
        driver.setActive(true);

        DriverResponseDTO dto = mapper.toDTO(driver);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Juan");
        assertThat(dto.getPhone()).isEqualTo("+34600000000");
        assertThat(dto.getBusId()).isEqualTo(10L);
        assertThat(dto.getBusLicensePlate()).isEqualTo("1234-ABC");
        assertThat(dto.getLicenceActive()).isTrue();
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    void toDTO_WithoutBus_ShouldHaveNullBusFields() {
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setName("Ana");
        driver.setPhone("+34600000001");

        DriverResponseDTO dto = mapper.toDTO(driver);

        assertThat(dto.getBusId()).isNull();
        assertThat(dto.getBusLicensePlate()).isNull();
    }

    @Test
    void toEntity_ShouldNotMapBus() {
        DriverRequestDTO dto = new DriverRequestDTO();
        dto.setName("Carlos");
        dto.setPhone("+34600000002");
        dto.setBusId(5L);

        Driver driver = mapper.toEntity(dto);

        assertThat(driver.getName()).isEqualTo("Carlos");
        assertThat(driver.getPhone()).isEqualTo("+34600000002");
        assertThat(driver.getBus()).isNull();
    }

    @Test
    void updateFromDto_ShouldNotUpdateBus() {
        Bus bus = new Bus();
        bus.setId(1L);

        Driver driver = new Driver();
        driver.setBus(bus);

        DriverRequestDTO dto = new DriverRequestDTO();
        dto.setBusId(5L);

        mapper.updateFromDto(dto, driver);

        assertThat(driver.getBus()).isSameAs(bus);
    }
}
