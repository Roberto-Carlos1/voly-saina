package com.voly_saina.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.Hibernate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "panier_details", schema = "voly_saina")
public class PanierDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_panier_details")
    @EqualsAndHashCode.Include
    private Long idPanierDetails;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "id_panier", nullable = false)
    private Panier panier;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "id_commande", nullable = true)
    private Commande commande;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "id_reservation_machine", nullable = true)
    private ReservationMachine reservationMachine;

    public PanierDetails orElse(Object object) {
        throw new UnsupportedOperationException("Unimplemented method 'orElse'");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PanierDetails that = (PanierDetails) o;
        return idPanierDetails != null && Objects.equals(idPanierDetails, that.idPanierDetails);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
