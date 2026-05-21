package com.inditex.g1_agencia_viajes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Long offerId;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "offer_type")
    private OfferType offerType = OfferType.GENERAL;

    @JsonIgnore
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<Travel> travels = new ArrayList<>();
}