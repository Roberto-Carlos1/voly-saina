package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
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
@Table(name = "facture", schema = "voly_saina")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture")
    @EqualsAndHashCode.Include
    private Long idFacture;

    @Column(name = "numero", nullable = false, length = 50, unique = true)
    private String numero;

    @Column(name = "type_operation", nullable = false, length = 30)
    private String typeOperation;

    @ToString.Exclude
    @OneToMany(mappedBy = "idFacture")
    private List<OperationMachine> operationMachine;

    @ToString.Exclude
    @OneToMany(mappedBy = "idFacture")
    private List<OperationProduit> operationProduit;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    private Utilisateur client;

    @CreationTimestamp
    @Column(name = "date_facture", nullable = false, updatable = false)
    private LocalDateTime dateFacture;

    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "montant_paye", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_facture", nullable = false)
    private StatutFacture statutFacture;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_panier", nullable = true)
    private Panier panier;

    @Column(name = "date_limite")
    private LocalDate dateLimite;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Facture that = (Facture) o;
        return idFacture != null && Objects.equals(idFacture, that.idFacture);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
