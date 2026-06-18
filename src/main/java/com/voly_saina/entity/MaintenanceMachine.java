package com.voly_saina.entity;

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
@Table(name = "maintenance_machine", schema = "voly_saina")
public class MaintenanceMachine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maintenance")
    private Long idMaintenance;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_machine", nullable = false)
    private Machine machine;
    
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @Column(name = "date_retour_prevue")
    private LocalDate dateRetourPrevue;
    
    @Column(name = "date_retour_reelle")
    private LocalDate dateRetourReelle;
    
    @Column(name = "cout", precision = 12, scale = 2)
    private BigDecimal cout = BigDecimal.ZERO;
    
    @Column(name = "travaux")
    private String travaux;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut_maintenance", nullable = false)
    private StatutMaintenance statutMaintenance;
}
