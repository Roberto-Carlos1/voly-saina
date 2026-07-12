package com.voly_saina.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeDTO {
    private Long idLigneCommande;
    private Long idCommande;
    private Long idProduit;
    private String nomProduit;
    private Integer quantite;
    private BigDecimal prixUnitaire;
}
