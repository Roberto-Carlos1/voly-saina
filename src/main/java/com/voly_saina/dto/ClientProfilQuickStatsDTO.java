package com.voly_saina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfilQuickStatsDTO {
    private Integer nbReservations;
    private Integer nbCommandes;
    private Integer nbFactures;
    private BigDecimal totalDepense;
}

