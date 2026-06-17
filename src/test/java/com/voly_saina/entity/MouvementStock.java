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
@Table(name = "mouvement_stock", schema = "voly_saina")
public class MouvementStock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mouvement")
    private Long idMouvement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;
    
    @Column(name = "type_mouvement", nullable = false)
    private String typeMouvement; // Valeurs: entree, sortie, correction
    
    @Column(name = "quantite", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantite;
    
    @Column(name = "motif")
    private String motif;
    
    @CreationTimestamp
    @Column(name = "date_mouvement", nullable = false, updatable = false)
    private LocalDateTime dateMouvement;
}