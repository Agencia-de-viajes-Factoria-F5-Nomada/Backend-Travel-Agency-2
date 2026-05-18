package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.TravelRequestDTO;
import com.inditex.g1_agencia_viajes.dto.TravelResponseDTO;
import com.inditex.g1_agencia_viajes.model.Hotel;
import com.inditex.g1_agencia_viajes.model.Travel;
import org.springframework.stereotype.Component;

@Component
public class TravelMapper {

    public Travel toEntity(TravelRequestDTO dto, Hotel hotel) {
        Travel travel = new Travel();
        travel.setDestiny(dto.getDestiny());
        travel.setStartDate(dto.getStartDate());
        travel.setEndDate(dto.getEndDate());
        travel.setSale(dto.getSale());
        travel.setAvailablePlaces(dto.getAvailablePlaces());
        travel.setHotel(hotel);
        return travel;
    }

    public TravelResponseDTO toDTO(Travel travel) {
        TravelResponseDTO dto = new TravelResponseDTO();
        dto.setId(travel.getId());
        dto.setDestiny(travel.getDestiny());
        dto.setStartDate(travel.getStartDate());
        dto.setEndDate(travel.getEndDate());
        dto.setSale(travel.getSale());
        dto.setAvailablePlaces(travel.getAvailablePlaces());
        dto.setActive(travel.getActive());

        if (travel.getHotel() != null) {
            dto.setHotelId(travel.getHotel().getId());
            dto.setHotelName(travel.getHotel().getName());
            dto.setHotelCity(travel.getHotel().getCity());
            dto.setHotelCountry(travel.getHotel().getCountry());
            dto.setHotelImageUrl(travel.getHotel().getImageUrl());
            dto.setHotelStars(travel.getHotel().getStars());
            dto.setHalfBoardPrice(travel.getHotel().getHalfBoardPrice());
            dto.setFullBoardPrice(travel.getHotel().getFullBoardPrice());
        }

        if (travel.getOffer() != null) {
            dto.setDiscountPercentage(travel.getOffer().getDiscountPercentage());
        }

        return dto;
    }
}
