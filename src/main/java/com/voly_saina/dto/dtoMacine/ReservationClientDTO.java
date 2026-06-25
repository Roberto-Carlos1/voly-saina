package com.voly_saina.dto.dtoMacine;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class ReservationClientDTO {
    private Long idReservation;
    private Long idMachine;
    private String machineNom;
    private String machineType;
    private BigDecimal prixJour;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieuLivraison;
    private BigDecimal prixTotal;
    private String statut;
    private String statutLibelle;
    private String motifRefus;
    private LocalDateTime dateCreation;
    private Boolean peutAnnuler;
    private Boolean peutRetourner;
    private Boolean estTerminee;
}