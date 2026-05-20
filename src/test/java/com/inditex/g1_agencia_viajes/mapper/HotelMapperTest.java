package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.HotelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.HotelResponseDTO;
import com.inditex.g1_agencia_viajes.model.Hotel;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class HotelMapperTest {

    private final HotelMapper mapper = Mappers.getMapper(HotelMapper.class);

    @Test
    void toDTO_ShouldMapAllFields() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Gran Hotel");
        hotel.setAddress("Calle Mayor 1");
        hotel.setCity("Madrid");
        hotel.setCountry("España");
        hotel.setStars(4);
        hotel.setCapacity(100);
        hotel.setAvailablePlaces(80);
        hotel.setHalfBoardPrice(100.0);
        hotel.setFullBoardPrice(150.0);
        hotel.setImageUrl("http://image.com");
        hotel.setActive(true);

        HotelResponseDTO dto = mapper.toDTO(hotel);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Gran Hotel");
        assertThat(dto.getAddress()).isEqualTo("Calle Mayor 1");
        assertThat(dto.getCity()).isEqualTo("Madrid");
        assertThat(dto.getCountry()).isEqualTo("España");
        assertThat(dto.getStars()).isEqualTo(4);
        assertThat(dto.getCapacity()).isEqualTo(100);
        assertThat(dto.getAvailablePlaces()).isEqualTo(80);
        assertThat(dto.getHalfBoardPrice()).isEqualTo(100.0);
        assertThat(dto.getFullBoardPrice()).isEqualTo(150.0);
        assertThat(dto.getImageUrl()).isEqualTo("http://image.com");
        assertThat(dto.getActive()).isTrue();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        HotelRequestDTO dto = new HotelRequestDTO();
        dto.setName("Hotel Sol");
        dto.setAddress("Playa 2");
        dto.setCity("Benidorm");
        dto.setCountry("España");
        dto.setStars(3);
        dto.setCapacity(80);
        dto.setAvailablePlaces(50);
        dto.setHalfBoardPrice(80.0);
        dto.setFullBoardPrice(120.0);
        dto.setImageUrl("http://image2.com");
        dto.setActive(true);

        Hotel hotel = mapper.toEntity(dto);

        assertThat(hotel.getName()).isEqualTo("Hotel Sol");
        assertThat(hotel.getAddress()).isEqualTo("Playa 2");
        assertThat(hotel.getCity()).isEqualTo("Benidorm");
        assertThat(hotel.getCountry()).isEqualTo("España");
        assertThat(hotel.getStars()).isEqualTo(3);
        assertThat(hotel.getCapacity()).isEqualTo(80);
        assertThat(hotel.getAvailablePlaces()).isEqualTo(50);
        assertThat(hotel.getHalfBoardPrice()).isEqualTo(80.0);
        assertThat(hotel.getFullBoardPrice()).isEqualTo(120.0);
        assertThat(hotel.getImageUrl()).isEqualTo("http://image2.com");
        assertThat(hotel.getActive()).isTrue();
    }

    @Test
    void updateFromDto_ShouldUpdateNonNullFields() {
        Hotel hotel = new Hotel();
        hotel.setName("Original");
        hotel.setStars(3);

        HotelRequestDTO dto = new HotelRequestDTO();
        dto.setName("Updated");

        mapper.updateFromDto(dto, hotel);

        assertThat(hotel.getName()).isEqualTo("Updated");
        assertThat(hotel.getStars()).isEqualTo(3);
    }
}
