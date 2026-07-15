package com.voly_saina.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopProduitDTO {
    private Long idProduit;
    private String nomProduit;
    private String categorie;
    private BigDecimal quantiteTotale;
    private BigDecimal totalDepense;
}
