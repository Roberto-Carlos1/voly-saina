package com.voly.saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pret_bancaire", schema = "voly_saina")
public class PretBancaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pret")
    private Long idPret;
    
    @Column(name = "banque", nullable = false, length = 150)
    private String banque;
    
    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;
    
    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;
    
    @Column(name = "taux_interet", precision = 6, scale = 2)
    private BigDecimal tauxInteret = BigDecimal.ZERO;
    
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @Column(name = "statut", nullable = false)
    private String statut; // Valeurs: en_cours, cloture, en_retard
}