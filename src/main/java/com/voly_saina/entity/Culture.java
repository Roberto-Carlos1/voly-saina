package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "culture", schema = "voly_saina")
public class Culture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_culture")
    @EqualsAndHashCode.Include
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
    
    @ToString.Exclude
    @OneToOne(mappedBy = "culture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FicheCulture ficheCulture;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Culture that = (Culture) o;
        return idCulture != null && Objects.equals(idCulture, that.idCulture);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
