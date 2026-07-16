package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "utilisateur", schema = "voly_saina")
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    @EqualsAndHashCode.Include
    private Long idUtilisateur;
    
    @Column(name = "nom", nullable = false, length = 150)
    private String nom;
    
    @Column(name = "telephone", length = 30)
    private String telephone;
    
    @Column(name = "email", length = 150, unique = true)
    private String email;
    
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role", nullable = false)
    private RoleUtilisateur role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_compte", nullable = false)
    private StatutCompte statutCompte;
    
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
    
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private ProfilUtilisateur profil;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Utilisateur that = (Utilisateur) o;
        return idUtilisateur != null && Objects.equals(idUtilisateur, that.idUtilisateur);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
