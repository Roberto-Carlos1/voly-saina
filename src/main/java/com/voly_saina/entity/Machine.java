package com.voly_saina.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

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

    @Column(name = "localisation")
    private String localisation;

    @Column(name = "kilometrage", precision = 12, scale = 2)
    private BigDecimal kilometrage = BigDecimal.ZERO;

    @Column(name = "disponible", nullable = false)
    private Boolean disponible = true;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @JsonIgnore
    @OneToMany(mappedBy = "machine")
    @OrderBy("dateCreation DESC, id DESC")
    private List<StatutMachine> statuts;

    

    public Machine() {
    }

    public Long getIdMachine() {
        return idMachine;
    }

    public void setIdMachine(Long idMachine) {
        this.idMachine = idMachine;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypeMachine getTypeMachine() {
        return typeMachine;
    }

    public void setTypeMachine(TypeMachine typeMachine) {
        this.typeMachine = typeMachine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrixJour() {
        return prixJour;
    }

    public void setPrixJour(BigDecimal prixJour) {
        this.prixJour = prixJour;
    }

    public StatutMachine getStatutMachine() {
        if (statuts == null || statuts.isEmpty()) {
            return null;
        }
        return statuts.get(0);
    }

    public void setStatutMachine(StatutMachine statutMachine) {
        if (statutMachine != null) {
            statutMachine.setMachine(this);
        }
        this.statuts = new ArrayList<>();
        if (statutMachine != null) {
            this.statuts.add(statutMachine);
        }
    }

    public EtatMachine getEtatMachine() {
        StatutMachine statutMachine = getStatutMachine();
        return statutMachine == null ? null : statutMachine.getEtatMachine();
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public BigDecimal getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(BigDecimal kilometrage) {
        this.kilometrage = kilometrage;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<StatutMachine> getStatuts() {
        return statuts;
    }

    public void setStatuts(List<StatutMachine> statuts) {
        this.statuts = statuts;
    }
}
