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
@Table(name = "commande", schema = "voly_saina")
public class Commande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commande")
    private Long idCommande;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    private Utilisateur client;
    
    @CreationTimestamp
    @Column(name = "date_commande", nullable = false, updatable = false)
    private LocalDateTime dateCommande;
    
    @Column(name = "adresse_livraison")
    private String adresseLivraison;
    
    @Column(name = "mode_paiement", length = 80)
    private String modePaiement;
    
    @Column(name = "statut", nullable = false)
    private String statut; // Valeurs: en_attente, validee, preparee, en_livraison, livree, annulee
    
    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;
}