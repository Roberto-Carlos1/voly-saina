package com.voly.saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "culture", schema = "voly_saina")
public class Culture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_culture")
    private Long idCulture;
    
    @Column(name = "nom", nullable = false, length = 120, unique = true)
    private String nom;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "saison_recommandee", length = 120)
    private String saisonRecommandee;
    
    @Column(name = "localisation_recommandee")
    private String localisationRecommandee;
    
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;
    
    @OneToOne(mappedBy = "culture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FicheCulture ficheCulture;
}