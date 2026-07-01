package com.voly_saina.entity.dto;

public class FactureDTO {
    private String nomClient;
    private int numeroPage;
    private String idStatut;

    public FactureDTO(String nomClient, int numeroPage, String idStatut) {
        this.nomClient = nomClient;
        this.numeroPage = numeroPage;
        this.idStatut = idStatut;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public int getNumeroPage() {
        return numeroPage;
    }

    public void setNumeroPage(int numeroPage) {
        this.numeroPage = numeroPage;
    }

    public String getIdStatut() {
        return idStatut;
    }

    public void setIdStatut(String idStatut) {
        this.idStatut = idStatut;
    }

}
