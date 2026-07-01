package com.voly_saina.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "panier_details", schema = "voly_saina")
public class PanierDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_panier_details")
    private Long idPanierDetails;

    @ManyToOne
    @JoinColumn(name = "id_panier", nullable = false)
    private Panier panier;

    @ManyToOne
    @JoinColumn(name = "id_commande", nullable = true)
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "id_reservation_machine", nullable = true)
    private ReservationMachine reservationMachine;
    
}
