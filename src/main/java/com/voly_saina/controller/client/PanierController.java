package com.voly_saina.controller.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.Panier;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.exception.PanierException;
import com.voly_saina.service.ModePaiementService;
import com.voly_saina.service.PanierService;
import com.voly_saina.service.UtilisateurService;

@Controller
@RequestMapping("/client/panier")
public class PanierController {

    @Autowired
    private PanierService panierService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private ModePaiementService modePaiementService;

    private Long resolveClientId(Long clientId) {
        return clientId != null ? clientId : 1L;
    }

    @GetMapping("/ajouter")
    public String ajouterAuPanier(
            @RequestParam("produitId") Long produitId,
            @RequestParam(value = "quantite", required = false, defaultValue = "1") BigDecimal quantite,
            @RequestParam(value = "idClient", required = false) Long clientId,
            Model model) {

        try {
            panierService.ajouterAuPanier(resolveClientId(clientId), produitId, quantite);
            return "redirect:/client/panier";
        } catch (PanierException e) {
            model.addAttribute("error", e.getMessage());
            return "client/panier";
        }
    }

    @GetMapping
    public String voirProduitsPanier(
            @RequestParam(value = "idClient", required = false) Long clientId,
            Model model) {

        try {
            Long idClientFinal = resolveClientId(clientId);

            Utilisateur client = utilisateurService.findById(idClientFinal)
                    .orElseThrow(() -> new PanierException("Client introuvable."));

            Panier panier = panierService.findCurrentPanierByIdClient(idClientFinal);

            List<ReservationMachine> reservations = panierService.getReservationsEnAttente(idClientFinal);

            BigDecimal totalReservations = reservations.stream()
                    .map(ReservationMachine::getPrixTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Commande commandePanier = panierService.findPendingCommande(idClientFinal);

            List<LigneCommande> lignes = List.of();
            BigDecimal totalCommandes = BigDecimal.ZERO;

            if (commandePanier != null) {
                lignes = panierService.getLignesFromPanier(panier);
                totalCommandes = commandePanier.getMontantTotal() != null
                        ? commandePanier.getMontantTotal()
                        : BigDecimal.ZERO;
            }

            model.addAttribute("commande", commandePanier);
            model.addAttribute("lignes", lignes);
            model.addAttribute("reservations", reservations);
            model.addAttribute("montantTotal", totalCommandes.add(totalReservations));
            model.addAttribute("modePaiements", modePaiementService.findAll());
            model.addAttribute("idPanier", panier.getIdPanier());
            model.addAttribute("idClient", idClientFinal);

            return "client/commandePanier";

        } catch (PanierException e) {
            model.addAttribute("error", e.getMessage());
            return "client/panier";
        }
    }

    @GetMapping("/details")
    public String voirPanier(
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {

        try {
            Long idClientFinal = resolveClientId(clientId);

            utilisateurService.findById(idClientFinal)
                    .orElseThrow(() -> new PanierException("Client introuvable."));

            Commande commandePanier = panierService.findPendingCommande(idClientFinal);

            if (commandePanier == null) {
                throw new PanierException("Aucune commande en attente.");
            }

            List<LigneCommande> lignes = panierService.getLignesByPanierId(1L);

            model.addAttribute("commande", commandePanier);
            model.addAttribute("lignes", lignes);
            model.addAttribute("montantTotal",
                    commandePanier.getMontantTotal() == null
                            ? BigDecimal.ZERO
                            : commandePanier.getMontantTotal());
            model.addAttribute("modePaiements", modePaiementService.findAll());

            return "client/commandePanier";

        } catch (PanierException e) {
            model.addAttribute("error", e.getMessage());
            return "client/panier";
        }
    }

    @PostMapping("/supprimer")
    public String supprimerLigne(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {

        try {
            Long idClientFinal = resolveClientId(clientId);

            panierService.supprimerLigne(ligneId, idClientFinal);

            Panier panier = panierService.findCurrentPanierByIdClient(idClientFinal);

            if (panier == null) {
                throw new PanierException("Panier introuvable.");
            }

            panierService.deleteById(panier.getIdPanier());

            return "redirect:/client/panier";

        } catch (PanierException e) {
            model.addAttribute("error", e.getMessage());
            return "client/panier";
        }
    }

    @PostMapping("/quantite")
    public String mettreAJourQuantite(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam("quantite") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {

        try {
            panierService.mettreAJourQuantite(ligneId, quantite, resolveClientId(clientId));
            return "redirect:/client/panier";

        } catch (PanierException e) {
            model.addAttribute("error", e.getMessage());
            return "client/panier";
        }
    }

    @PostMapping("/valider")
    public String cloturerPanier(
            @RequestParam("adresseLivraison") String adresseLivraison,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "idPanier", required = false) Long idPanier,
            Model model) {

        try {
            Long idClientFinal = resolveClientId(clientId);

            panierService.cloturerPanier(idClientFinal, idPanier, adresseLivraison);

            return "redirect:/client/factures?idClient=" + idClientFinal;

        } catch (PanierException e) {
            model.addAttribute("error", e.getMessage());
            return "client/panier";
        }
    }

    @GetMapping("/recap")
    public String recapCommande(
            @RequestParam("commandeId") Long commandeId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {

        try {
            Long idClientFinal = resolveClientId(clientId);

            Commande commande = panierService.findCommandeById(commandeId, idClientFinal);

            if (commande == null) {
                throw new PanierException("Commande introuvable.");
            }

            List<LigneCommande> lignes = panierService.getLignesByCommande(commande);

            BigDecimal montantTotal = commande.getMontantTotal() != null
                    ? commande.getMontantTotal()
                    : BigDecimal.ZERO;

            model.addAttribute("commande", commande);
            model.addAttribute("lignes", lignes);
            model.addAttribute("montantTotal", montantTotal);

            return "client/recu/recap-commande";

        } catch (PanierException e) {
            model.addAttribute("error", e.getMessage());
            return "client/recu/recap-commande";
        }
    }
}