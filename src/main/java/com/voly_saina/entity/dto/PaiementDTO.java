package com.voly_saina.entity.dto;

public class PaiementDTO {
    private String idFacture;
    private String montant;
    private String date;
    private String modePaiement;
    private String idClient;

    public PaiementDTO(String idFacture, String montant, String date, String modePaiement, String idClient) {
        this.idFacture = idFacture;
        this.montant = montant;
        this.date = date;
        this.modePaiement = modePaiement;
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

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

}
