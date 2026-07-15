package com.voly_saina.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureClientDTO {
    private Long idFacture;
    private String numero;
    private String typeOperation;
    private Long idOperation;
    private LocalDateTime dateFacture;
    private BigDecimal montantTotal;
    private BigDecimal montantPaye;
    private BigDecimal montantRestant;
    private String statutCode;
    private String statutLibelle;
    private LocalDate dateLimite;
    private boolean enRetard;
    private Integer pourcentagePaye;
}
