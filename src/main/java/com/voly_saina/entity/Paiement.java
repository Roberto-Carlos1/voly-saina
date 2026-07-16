package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "paiement", schema = "voly_saina")
public class Paiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    @EqualsAndHashCode.Include
    private Long idPaiement;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facture", nullable = false)
    private Facture facture;
    
    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;
    
    @CreationTimestamp
    @Column(name = "date_paiement", nullable = false, updatable = false)
    private LocalDateTime datePaiement;
    
    @Column(name = "reference", length = 120)
    private String reference;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mode_paiement")
    private ModePaiement modePaiement;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Paiement that = (Paiement) o;
        return idPaiement != null && Objects.equals(idPaiement, that.idPaiement);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
