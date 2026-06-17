package com.voly_saina.entity;

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
@Table(name = "remboursement_pret", schema = "voly_saina")
public class RemboursementPret {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_remboursement")
    private Long idRemboursement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pret", nullable = false)
    private PretBancaire pret;
    
    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;
    
    @Column(name = "date_remboursement", nullable = false)
    private LocalDate dateRemboursement;
    
    @Column(name = "reference_bancaire", length = 120)
    private String referenceBancaire;
    
    @Column(name = "justificatif")
    private String justificatif;
}