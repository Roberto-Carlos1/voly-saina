package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operation_produit", schema = "voly_saina")
public class OperationProduit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operation")
    private Long idOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit")
    private Produit idProduit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facture")
    private Facture idFacture;

    @Column(name = "quantite")
    private Long quantite;
}
