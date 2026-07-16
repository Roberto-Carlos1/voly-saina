package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "fiche_culture", schema = "voly_saina")
public class FicheCulture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fiche")
    @EqualsAndHashCode.Include
    private Long idFiche;
    
    @ToString.Exclude
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FicheCulture that = (FicheCulture) o;
        return idFiche != null && Objects.equals(idFiche, that.idFiche);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
