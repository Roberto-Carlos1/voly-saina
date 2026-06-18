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
@Table(name = "produit", schema = "voly_saina")
public class Produit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long idProduit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categorie")
    private CategorieProduit categorie;
    
    @Column(name = "nom", nullable = false, length = 150)
    private String nom;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "conseil_usage")
    private String conseilUsage;
    
    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;
    
    @Column(name = "stock", nullable = false, precision = 12, scale = 2)
    private BigDecimal stock = BigDecimal.ZERO;
    
    @Column(name = "seuil_stock", nullable = false, precision = 12, scale = 2)
    private BigDecimal seuilStock = BigDecimal.ZERO;
    
    @Column(name = "date_expiration")
    private LocalDate dateExpiration;
    
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;
}