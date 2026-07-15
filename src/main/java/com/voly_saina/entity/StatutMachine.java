package com.voly_saina.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "statut_machine", schema = "voly_saina")
public class StatutMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_machine", nullable = false)
    private Machine machine;

    @ManyToOne
    @JoinColumn(name = "id_etat_machine", nullable = false)
    private EtatMachine etatMachine;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    public StatutMachine() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public EtatMachine getEtatMachine() {
        return etatMachine;
    }

    public void setEtatMachine(EtatMachine etatMachine) {
        this.etatMachine = etatMachine;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
    }
}
