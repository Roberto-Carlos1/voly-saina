package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statut_reservation", schema = "voly_saina")
public class StatutReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut_reservation")
    private Long idStatutReservation;

    @Column(name = "code", nullable = false, length = 30, unique = true)
    private String code;

    @Column(name = "libelle", length = 80)
    private String libelle;
}
