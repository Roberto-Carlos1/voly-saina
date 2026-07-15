package com.voly_saina.exception;

public class PanierException extends RuntimeException {

    // 📦 Messages liés aux Produits et Stocks
    public static final String PRODUIT_INTROUVABLE = "Le produit spécifié n'existe pas.";
    public static final String PRODUIT_EPUISE = "Désolé, ce produit n'est plus disponible en stock.";
    public static final String STOCK_INSUFFISANT = "La quantité demandée dépasse le stock disponible.";
    public static final String QUANTITE_INVALIDE = "La quantité ajoutée doit être supérieure à zéro.";

    // 🛒 Messages liés au Panier et Commandes
    public static final String PANIER_INTROUVABLE = "Votre panier actuel est introuvable ou a expiré.";
    public static final String COMMANDE_INTROUVABLE = "Aucune commande en attente n'a été trouvée pour ce client.";
    public static final String LIGNE_INTROUVABLE = "L'article que vous tentez de modifier n'existe pas dans le panier.";
    public static final String PANIER_DEJA_CLOTURE = "Ce panier a déjà été validé et converti en facture.";

    // 📆 Messages liés aux Machines et Réservations
    public static final String MACHINE_INTROUVABLE = "La machine demandée est introuvable.";
    public static final String MACHINE_INDISPONIBLE = "Cette machine est déjà réservée pour les dates sélectionnées.";
    public static final String DATES_INVALIDES = "La date de fin de réservation doit être après la date de début.";

    // 👤 Messages liés aux Utilisateurs
    public static final String CLIENT_INTROUVABLE = "Le profil client est introuvable.";
    public static final String NON_CONNECTE = "Vous devez être connecté pour accéder à votre panier.";

    // Constructeurs
    public static final String ERREUR_INTERNE = "Une erreur est survenue lors du traitement de votre panier.";

    // Dans votre classe PanierException existante :
    public static final String SAISIE_VIDE = "La quantité ne peut pas être vide. Veuillez saisir un nombre entier.";
    public static final String SAISIE_NON_NUMERIQUE = "La quantité saisie est invalide. Veuillez entrer uniquement des chiffres.";

    public PanierException(String message) {
        super(message);
    }

    public PanierException(String message, Throwable cause) {
        super(message, cause);
    }
}