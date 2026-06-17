package com.voly.saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categorie_produit", schema = "voly_saina")
public class CategorieProduit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie")
    private Long idCategorie;
    
    @Column(name = "nom", nullable = false, length = 120, unique = true)
    private String nom;
    
    @Column(name = "description")
    private String description;
}