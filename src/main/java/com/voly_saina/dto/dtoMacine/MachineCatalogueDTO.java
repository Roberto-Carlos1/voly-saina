package com.voly_saina.dto.dtoMacine;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MachineCatalogueDTO {
    private Long idMachine;
    private String nom;
    private String description;
    private BigDecimal prixJour;
    private String localisation;
    private String typeMachine;
    private String etatMachine;
    private Boolean disponible;
    private String imageUrl;
}