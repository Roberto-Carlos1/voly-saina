package com.voly_saina.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepenseMensuelleDTO {
    private int annee;
    private int mois;
    private String libelleMois;
    private BigDecimal montant;
}
