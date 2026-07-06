package com.voly_saina.exception;

public class PaiementException extends Exception {
    public static final String MONTANT_VIDE = "Le montant est obligatoire";
    public static final String MONTANT_NEGATIF = "Le montant est invalide";

    public static final String DATE_ANTERIEUR = "La date de paiement doit etre apres la date de creation de la facture";

    public PaiementException(String message) {
        super(message);
    }

}
