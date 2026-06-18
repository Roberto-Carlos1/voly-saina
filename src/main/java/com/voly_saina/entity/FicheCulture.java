package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fiche_culture", schema = "voly_saina")
public class FicheCulture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fiche")
    private Long idFiche;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_culture", nullable = false)
    private Culture culture;
    
    @Column(name = "periode_plantation")
    private String periodePlantation;
    
    @Column(name = "duree_avant_recolte")
    private String dureeAvantRecolte;
    
    @Column(name = "preparation_sol")
    private String preparationSol;
    
    @Column(name = "quantite_semence")
    private String quantiteSemence;
    
    @Column(name = "engrais_recommandes")
    private String engraisRecommandes;
    
    @Column(name = "arrosage")
    private String arrosage;
    
    @Column(name = "maladies_courantes")
    private String maladiesCourantes;
    
    @Column(name = "conseils_pratiques")
    private String conseilsPratiques;
    
    @Column(name = "valide", nullable = false)
    private Boolean valide = false;
    
    @CreationTimestamp
    @Column(name = "date_mise_a_jour", nullable = false, updatable = false)
    private LocalDateTime dateMiseAJour;
}