package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "profil_utilisateur", schema = "voly_saina")
public class ProfilUtilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profil")
    @EqualsAndHashCode.Include
    private Long idProfil;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false, unique = true)
    @ToString.Exclude
    private Utilisateur utilisateur;
    
    @Column(name = "genre", length = 30)
    private String genre;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "csp", length = 80)
    private String csp;
    
    @Column(name = "localisation")
    private String localisation;
    
    @Column(name = "niveau_connexion", length = 50)
    private String niveauConnexion;
    
    @UpdateTimestamp
    @Column(name = "date_mise_a_jour", nullable = false)
    private LocalDateTime dateMiseAJour;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProfilUtilisateur that = (ProfilUtilisateur) o;
        return idProfil != null && Objects.equals(idProfil, that.idProfil);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}