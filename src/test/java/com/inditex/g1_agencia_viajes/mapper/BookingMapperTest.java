package com.inditex.g1_agencia_viajes.mapper;

import com.inditex.g1_agencia_viajes.dto.BookingResponseDTO;
import com.inditex.g1_agencia_viajes.model.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void toDTO_ShouldMapAllFields() {
        Booking booking = getBooking();

        BookingResponseDTO dto = mapper.toDTO(booking);

        assertThat(dto.getBookingId()).isEqualTo(1L);
        assertThat(dto.getTravelId()).isEqualTo(1L);
        assertThat(dto.getTravelDestiny()).isEqualTo("París");
        assertThat(dto.getCustomerIds()).containsExactly(10L, 20L);
        assertThat(dto.getEmployeeId()).isEqualTo(100L);
        assertThat(dto.getTypeBoard()).isEqualTo("HALF");
        assertThat(dto.getIsGroup()).isFalse();
        assertThat(dto.getTotalPrice()).isEqualTo(500.0);
    }

    private static @NonNull Booking getBooking() {
        Travel travel = new Travel();
        travel.setId(1L);
        travel.setDestiny("París");

        User user1 = new User();
        user1.setId(10L);
        user1.setName("Alice");
        User user2 = new User();
        user2.setId(20L);
        user2.setName("Bob");

        Employee employee = new Employee();
        employee.setEmployeeId(100L);

        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setBoughtDate(LocalDateTime.of(2026, 6, 1, 10, 0));
        booking.setTypeBoard(TypeBoard.HALF);
        booking.setIsGroup(false);
        booking.setTotalPrice(500.0);
        booking.setTravel(travel);
        booking.setCustomers(List.of(user1, user2));
        booking.setEmployee(employee);
        return booking;
    }

    @Test
    void customersToIds_WithNull_ShouldReturnNull() {
        List<Long> result = mapper.customersToIds(null);
        assertThat(result).isNull();
    }

    @Test
    void customersToIds_WithEmptyList_ShouldReturnEmptyList() {
        List<Long> result = mapper.customersToIds(List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void customersToIds_ShouldMapIds() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        List<Long> result = mapper.customersToIds(List.of(user1, user2));

        assertThat(result).containsExactly(1L, 2L);
    }
}
