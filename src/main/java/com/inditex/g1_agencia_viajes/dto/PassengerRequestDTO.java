package com.inditex.g1_agencia_viajes.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PassengerRequestDTO {
    private String name;
    private String surname;
    private LocalDate birthDate;
}