package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.BookingResponseDTO;
import com.inditex.g1_agencia_viajes.model.Booking;
import com.inditex.g1_agencia_viajes.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "travelId", source = "travel.id")
    @Mapping(target = "travelDestiny", source = "travel.destiny")
    @Mapping(target = "customerIds", source = "customers", qualifiedByName = "customersToIds")
    @Mapping(target = "employeeId", source = "employee.employeeId")
    BookingResponseDTO toDTO(Booking booking);

    default List<BookingResponseDTO> toDTOList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Named("customersToIds")
    default List<Long> customersToIds(List<User> customers) {
        if (customers == null) {
            return null;
        }
        return customers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
