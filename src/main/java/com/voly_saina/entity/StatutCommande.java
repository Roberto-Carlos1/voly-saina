package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statut_commande", schema = "voly_saina")
public class StatutCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_commande")
    private Long idStatutCommande;

    @Column(name = "code", nullable = false, length = 30, unique = true)
    private String code;

    @Column(name = "libelle", length = 80)
    private String libelle;
}
