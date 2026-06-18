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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_machine", nullable = false)
    private TypeMachine typeMachine;

    @Column(name = "description")
    private String description;

    @Column(name = "prix_jour", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixJour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etat_machine", nullable = false)
    private EtatMachine etatMachine;

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
