package com.inditex.g1_agencia_viajes.dto;

import com.inditex.g1_agencia_viajes.model.TypeBoard;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequestDTO {

    private LocalDateTime boughtDate;

    @NotNull(message = "El tipo de pensión es obligatorio")
    private TypeBoard typeBoard;

    private Boolean isGroup;

    private Double totalPrice;

    @NotNull(message = "El viaje es obligatorio")
    private Long travelId;

    @NotEmpty
    private List<Long> customerIds;

    private Long employeeId;
}
