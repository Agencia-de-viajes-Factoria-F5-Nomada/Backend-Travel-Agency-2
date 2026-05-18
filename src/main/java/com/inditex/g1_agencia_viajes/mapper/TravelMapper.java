package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.TravelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TravelResponseDTO;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.Travel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TravelMapper {

    default Travel toEntity(TravelRequestDTO dto, Hotel hotel) {
        Travel travel = toEntity(dto);
        travel.setHotel(hotel);
        return travel;
    }

    Travel toEntity(TravelRequestDTO dto);

    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "hotelName", source = "hotel.name")
    @Mapping(target = "hotelCity", source = "hotel.city")
    @Mapping(target = "hotelCountry", source = "hotel.country")
    @Mapping(target = "hotelImageUrl", source = "hotel.imageUrl")
    @Mapping(target = "halfBoardPrice", source = "hotel.halfBoardPrice")
    @Mapping(target = "fullBoardPrice", source = "hotel.fullBoardPrice")
    TravelResponseDTO toDTO(Travel travel);

    @Mapping(target = "hotel", ignore = true)
    void updateFromDto(TravelRequestDTO dto, @MappingTarget Travel travel);
}
