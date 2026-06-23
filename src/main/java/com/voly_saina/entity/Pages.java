package com.voly_saina.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pages", schema = "voly_saina")
public class Pages {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_page")
    private Long idPage;
    
    @Column(name = "nombre")
    private int nombre;
}