package com.inditex.g1_agencia_viajes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_trip_requests")
@Getter
@Setter
@NoArgsConstructor
public class CustomTripRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "El destino es obligatorio")
    @Column(name = "preference_summary", length = 2000)
    private String preferenceSummary;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Min(value = 1, message = "Debe haber al menos 1 viajero")
    @Column(name = "passengers")
    private Integer passengers;

    @Column(name = "includes_flight")
    private Boolean includesFlight;

    @Column(name = "includes_hotel")
    private Boolean includesHotel;

    @Column(name = "includes_activities")
    private Boolean includesActivities;

    @Column(name = "bought_date")
    private LocalDateTime boughtDate;

    @Column(name = "is_group")
    private Boolean isGroup;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status")
    private String status = "PENDING";

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Column(name = "type_board", length = 20)
    private String typeBoard;

    @Column(name = "base_price_per_passenger", precision = 10, scale = 2)
    private BigDecimal basePricePerPassenger;

    @Column(name = "total_before_discount", precision = 10, scale = 2)
    private BigDecimal totalBeforeDiscount;

    @Column(name = "total_discount", precision = 10, scale = 2)
    private BigDecimal totalDiscount;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;
}