package com.voly_saina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeClientDTO {
    private Long idCommande;
    private String numeroCommande;
    private String statutCommande;
    private String dateCommande;
    private String dateLivraison;
    private String modePaiement;
    private String adresseLivraison;
}
