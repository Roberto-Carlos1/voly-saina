package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "retour_machine", schema = "voly_saina")
public class RetourMachine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retour")
    @EqualsAndHashCode.Include
    private Long idRetour;
    
    @ToString.Exclude
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RetourMachine that = (RetourMachine) o;
        return idRetour != null && Objects.equals(idRetour, that.idRetour);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
