package com.voly_saina.dto.dtoMacine;

import lombok.Data;
import java.time.LocalDate;
import java.math.BigDecimal;

@Data
public class RetourClientDTO {
    private Long idRetour;
    private Long idReservation;
    private Long idMachine;
    private String machineNom;
    private LocalDate dateRetour;
    private String etatRetour;
    private String etatRetourLibelle;
    private String remarque;
    private BigDecimal penalite;
    private Boolean estRetard;
    private Long joursRetard;
}