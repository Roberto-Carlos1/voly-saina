package com.voly_saina.dto;

import com.voly_saina.entity.CategorieProduit;
import java.math.BigDecimal;

public class CatalogueProduitDTO {

    private Long idProduit;
    private String nom;
    private String description;
    private BigDecimal stock;
    private java.time.LocalDate dateExpiration;
    private boolean actif;
    private boolean disponible;
    private CategorieProduit categorie;

    public Long getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(Long idProduit) {
        this.idProduit = idProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public java.time.LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(java.time.LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public CategorieProduit getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieProduit categorie) {
        this.categorie = categorie;
    }
}

