package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mode_paiement", schema = "voly_saina")
public class ModePaiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mode_paiement")
    private Long idModePaiement;
        
    @Column(name = "libelle")
    private String libelle;
    
}
