package com.voly_saina.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.Facture;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.repository.PanierRepository;

@Service
public class PanierService {

    @Autowired
    private FactureService factureService;

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private PanierCommandeService panierCommandeService;

    @Autowired
    private PanierReservationService panierReservationService;

    // ============ PANIER CRUD ============

    public List<Panier> findAll() {
        return panierRepository.findAll();
    }

    public Panier findById(Long id) {
        return panierRepository.findById(id).orElse(null);
    }

    public Panier save(Panier panier) {
        return panierRepository.save(panier);
    }

    public boolean existsById(Long id) {
        return panierRepository.existsById(id);
    }

    public void deleteById(Long id) {
        panierRepository.deleteById(id);
    }

    public Panier findCurrentPanierByIdClient(Long idClient) {
        Panier retour = panierRepository.findPanierActifPlusRecent(idClient);
        if (retour == null) {
            Utilisateur u = utilisateurService.findById(idClient).orElse(null);
            retour = new Panier();
            retour.setClient(u);
            retour.setActif(true);
            save(retour);
        }
        return retour;
    }

    public Panier findByClientId(Long clientId) {
        return panierRepository.findFirstByClientIdUtilisateurOrderByIdPanierDesc(clientId);
    }

    public Panier cloturePanier(Long idClient) {
        Panier panier = findCurrentPanierByIdClient(idClient);
        if (panier != null) {
            panier.setActif(false);
            save(panier);
        }
        return panier;
    }

    // ============ PRODUITS (délègue à PanierCommandeService) ============

    public Commande ajouterAuPanier(Long clientId, Long produitId, BigDecimal quantite) {
        return panierCommandeService.ajouterAuPanier(clientId, produitId, quantite);
    }

    public Commande findPendingCommande(Long clientId) {
        return panierCommandeService.findPendingCommande(clientId);
    }

    public List<LigneCommande> getLignesFromPanier(Panier panier) {
        return panierCommandeService.getLignesFromPanier(panier);
    }

    public List<LigneCommande> getLignesByCommande(Commande commande) {
        return panierCommandeService.getLignesByCommande(commande);
    }

    public List<LigneCommande> getLignesByPanierId(Long idPanier) {
        return panierCommandeService.getLignesByPanierId(idPanier);
    }

    public void supprimerLigne(Long ligneId, Long clientId) {
        panierCommandeService.supprimerCommande(ligneId, clientId);
    }

    public void mettreAJourQuantite(Long ligneId, BigDecimal quantite, Long clientId) {
        panierCommandeService.mettreAJourQuantite(ligneId, quantite, clientId);
    }

    public Commande findCommandeById(Long commandeId, Long clientId) {
        return panierCommandeService.findCommandeById(commandeId, clientId);
    }

    public void recalculerTotal(Commande commande) {
        panierCommandeService.recalculerTotal(commande);
    }

    // ============ RÉSERVATIONS (délègue à PanierReservationService) ============

    public ReservationMachine ajouterReservationAuPanier(Long clientId, Long machineId,
            LocalDate dateDebut, LocalDate dateFin, String lieuLivraison) {
        return panierReservationService.ajouterReservationAuPanier(
                clientId, machineId, dateDebut, dateFin, lieuLivraison);
    }

    public void supprimerReservationDuPanier(Long reservationId, Long clientId) {
        panierReservationService.supprimerReservationDuPanier(reservationId, clientId);
    }

    public void modifierDatesReservation(Long reservationId, Long clientId,
            LocalDate dateDebut, LocalDate dateFin) {
        panierReservationService.modifierDatesReservation(reservationId, clientId, dateDebut, dateFin);
    }

    public int validerReservationsDuPanier(Long clientId) {
        return panierReservationService.validerReservationsDuPanier(clientId ,null);
    }

    public int viderReservationsDuPanier(Long clientId) {
        return panierReservationService.viderReservationsDuPanier(clientId);
    }

    public List<ReservationMachine> getReservationsEnAttente(Long clientId) {
        return panierReservationService.getReservationsEnAttente(clientId);
    }

    // ============ VALIDATION GLOBALE ============

    public void cloturerPanier(Long clientId, Long idPanier, String adresseLivraison) {
        utilisateurService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Facture facture = factureService.genererFactureProformat(clientId, idPanier);
        panierCommandeService.cloturerPanier(clientId, idPanier, adresseLivraison, facture);
        panierReservationService.validerReservationsDuPanier(clientId, facture);
    }
}
