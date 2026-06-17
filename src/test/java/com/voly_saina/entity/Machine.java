package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "machine", schema = "voly_saina")
public class Machine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_machine")
    private Long idMachine;
    
    @Column(name = "nom", nullable = false, length = 150)
    private String nom;
    
    @Column(name = "type_machine", nullable = false, length = 80)
    private String typeMachine;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "prix_jour", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixJour;
    
    @Column(name = "etat", nullable = false)
    private String etat; // Valeurs: disponible, louee, maintenance, hors_service
    
    @Column(name = "localisation")
    private String localisation;
    
    @Column(name = "kilometrage", precision = 12, scale = 2)
    private BigDecimal kilometrage = BigDecimal.ZERO;
    
    @Column(name = "disponible", nullable = false)
    private Boolean disponible = true;
    
    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
}