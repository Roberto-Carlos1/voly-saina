package com.voly_saina.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operation_machine", schema = "voly_saina")
public class OperationMachine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operation")
    private Long idOperation;
        
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_machine")
    private Machine idMachine;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facture")
    private Facture idFacture;

    @Column(name = "quantite")
    private Long quantite;
}
