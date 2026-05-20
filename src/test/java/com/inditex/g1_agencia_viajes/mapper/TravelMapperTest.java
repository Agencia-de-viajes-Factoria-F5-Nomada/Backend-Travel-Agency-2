package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.TravelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TravelResponseDTO;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.Travel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TravelMapperTest {

    private final TravelMapper mapper = Mappers.getMapper(TravelMapper.class);

    @Test
    void toDTO_ShouldMapAllFieldsWithHotel() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Hotel París");
        hotel.setCity("París");
        hotel.setCountry("Francia");
        hotel.setImageUrl("http://image.com/hotel");
        hotel.setHalfBoardPrice(100.0);
        hotel.setFullBoardPrice(150.0);

        Travel travel = new Travel();
        travel.setId(1L);
        travel.setDestiny("París Clásico");
        travel.setStartDate(LocalDate.of(2026, 7, 1));
        travel.setEndDate(LocalDate.of(2026, 7, 7));
        travel.setAvailablePlaces(20);
        travel.setHotel(hotel);
        travel.setSale(false);
        travel.setActive(true);

        TravelResponseDTO dto = mapper.toDTO(travel);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDestiny()).isEqualTo("París Clásico");
        assertThat(dto.getStartDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(dto.getEndDate()).isEqualTo(LocalDate.of(2026, 7, 7));
        assertThat(dto.getAvailablePlaces()).isEqualTo(20);
        assertThat(dto.getSale()).isFalse();
        assertThat(dto.getActive()).isTrue();
        assertThat(dto.getHotelId()).isEqualTo(1L);
        assertThat(dto.getHotelName()).isEqualTo("Hotel París");
        assertThat(dto.getHotelCity()).isEqualTo("París");
        assertThat(dto.getHotelCountry()).isEqualTo("Francia");
        assertThat(dto.getHotelImageUrl()).isEqualTo("http://image.com/hotel");
        assertThat(dto.getHalfBoardPrice()).isEqualTo(100.0);
        assertThat(dto.getFullBoardPrice()).isEqualTo(150.0);
    }

    @Test
    void toEntity_WithHotel_ShouldSetHotel() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        TravelRequestDTO dto = new TravelRequestDTO();
        dto.setDestiny("Londres");
        dto.setStartDate(LocalDate.of(2026, 8, 1));
        dto.setEndDate(LocalDate.of(2026, 8, 5));
        dto.setAvailablePlaces(30);

        Travel travel = mapper.toEntity(dto, hotel);

        assertThat(travel.getDestiny()).isEqualTo("Londres");
        assertThat(travel.getStartDate()).isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(travel.getEndDate()).isEqualTo(LocalDate.of(2026, 8, 5));
        assertThat(travel.getAvailablePlaces()).isEqualTo(30);
        assertThat(travel.getHotel()).isSameAs(hotel);
    }

    @Test
    void updateFromDto_ShouldNotUpdateHotel() {
        Hotel originalHotel = new Hotel();
        originalHotel.setId(1L);

        Travel travel = new Travel();
        travel.setHotel(originalHotel);
        travel.setDestiny("Original");

        TravelRequestDTO dto = new TravelRequestDTO();
        dto.setDestiny("Updated");

        mapper.updateFromDto(dto, travel);

        assertThat(travel.getDestiny()).isEqualTo("Updated");
        assertThat(travel.getHotel()).isSameAs(originalHotel);
    }
}
