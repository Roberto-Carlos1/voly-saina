package com.voly_saina.dto.dtoMacine;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FormulaireRetourDTO {
    // Réservation
    private Long reservationId;
    private Long clientId;
    private Long machineId;
    private String machineNom;
    private String machineDescription;
    private String machineType;
    private BigDecimal prixJour;
    private BigDecimal prixTotal;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateRetour;
    
    // Pénalités
    private BigDecimal penaliteRetard;
    private boolean estRetard;
    private long joursRetard;
    
    // États possibles
    private EtatOption[] etatsPossibles;
    
    @Data
    public static class EtatOption {
        private String code;
        private String libelle;
        private String icon;
        private String color;
        private String description;
        private BigDecimal penalite;
    }
}