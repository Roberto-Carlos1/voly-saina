package com.voly_saina.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesClientDTO {
    private BigDecimal totalDepenses;
    private BigDecimal totalDepensesLocations;
    private BigDecimal totalDepensesCommandes;
    private int nombreLocations;
    private int nombreCommandes;
    private int nombreFactures;
    private List<TopMachineDTO> topMachines;
    private List<TopProduitDTO> topProduits;
    private List<DepenseMensuelleDTO> depensesMensuelles;
}
