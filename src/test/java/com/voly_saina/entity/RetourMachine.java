package com.voly.saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "retour_machine", schema = "voly_saina")
public class RetourMachine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retour")
    private Long idRetour;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation", nullable = false, unique = true)
    private ReservationMachine reservation;
    
    @Column(name = "date_retour", nullable = false)
    private LocalDate dateRetour;
    
    @Column(name = "etat_retour")
    private String etatRetour;
    
    @Column(name = "remarque")
    private String remarque;
    
    @Column(name = "penalite", precision = 12, scale = 2)
    private BigDecimal penalite = BigDecimal.ZERO;
}