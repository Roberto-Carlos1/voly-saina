package com.voly.saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paiement", schema = "voly_saina")
public class Paiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Long idPaiement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facture", nullable = false)
    private Facture facture;
    
    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;
    
    @CreationTimestamp
    @Column(name = "date_paiement", nullable = false, updatable = false)
    private LocalDateTime datePaiement;
    
    @Column(name = "reference", length = 120)
    private String reference;
    
    @Column(name = "mode_paiement", length = 80)
    private String modePaiement;
}