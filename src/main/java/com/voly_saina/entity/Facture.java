package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.query.common.FetchClauseType;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facture", schema = "voly_saina")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture")
    private Long idFacture;

    @Column(name = "numero", nullable = false, length = 50, unique = true)
    private String numero;

    @Column(name = "type_operation", nullable = false, length = 30)
    private String typeOperation; // Valeurs: location, commande

    @OneToMany(mappedBy = "idFacture")
    private List<OperationMachine> operationMachine;

    @OneToMany(mappedBy = "idFacture")
    private List<OperationProduit> operationProduit;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_facture", nullable = false)
    private StatutFacture statutFacture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_panier", nullable = true)
    private Panier panier;

    @Column(name = "date_limite")
    private LocalDate dateLimite;

}
