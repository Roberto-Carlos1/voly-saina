package com.voly_saina.entity.dto;

public class PaiementDTO {
    private String idFacture;
    private String montant;
    private String date;
    private String mode;

    public PaiementDTO(String idFacture, String montant, String date, String mode) {
        this.idFacture = idFacture;
        this.montant = montant;
        this.date = date;
        this.mode = mode;
    }

    public String getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(String idFacture) {
        this.idFacture = idFacture;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
