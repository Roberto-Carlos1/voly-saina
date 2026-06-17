package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rapport", schema = "voly_saina")
public class Rapport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rapport")
    private Long idRapport;
    
    @Column(name = "type_rapport", nullable = false, length = 80)
    private String typeRapport;
    
    @Column(name = "periode_debut")
    private LocalDate periodeDebut;
    
    @Column(name = "periode_fin")
    private LocalDate periodeFin;
    
    @Column(name = "contenu", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String contenu;
    
    @CreationTimestamp
    @Column(name = "date_generation", nullable = false, updatable = false)
    private LocalDateTime dateGeneration;
}