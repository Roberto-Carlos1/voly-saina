package com.voly_saina.entity.view;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Immutable
@Table(name = "v_facture_fille", schema = "voly_saina")
public class FactureFille {

    @Id
    @Column(name = "id_operation_key")
    private String idOperationKey;

    @Column(name = "id_facture")
    private Integer idFacture;

    @Column(name = "numero")
    private String numero;
    
    @Column(name = "type_operation")
    private String typeOperation;

    @Column(name = "id_operation")
    private Integer idOperation;

    @Column(name = "date_operation")
    private LocalDateTime dateOperation;

    @Column(name = "montant_total")
    private BigDecimal montantTotal;

    @Column(name = "statut")
    private String statut;
}