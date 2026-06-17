package com.voly.saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profil_utilisateur", schema = "voly_saina")
public class ProfilUtilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profil")
    private Long idProfil;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", nullable = false, unique = true)
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
}