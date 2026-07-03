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
            @RequestParam(value = "idClient", required = false) Long clientId) {
        panierService.ajouterAuPanier(resolveClientId(clientId), produitId, quantite);
        return "redirect:/client/panier";
    }

    @GetMapping
    public String voirProduitsPanier(
            @RequestParam(value = "idClient", required = false) Long clientId,
            Model model) {
        Long idClientFinal = resolveClientId(clientId);
        Utilisateur client = utilisateurService.findById(idClientFinal).orElse(null);
        if (client == null) {
            model.addAttribute("error", "Client non trouvé");
            return "client/panier";
        }

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
                    ? commandePanier.getMontantTotal() : BigDecimal.ZERO;
        }

        model.addAttribute("commande", commandePanier);
        model.addAttribute("lignes", lignes);
        model.addAttribute("reservations", reservations);
        model.addAttribute("montantTotal", totalCommandes.add(totalReservations));
        model.addAttribute("modePaiements", modePaiementService.findAll());
        model.addAttribute("idPanier", panier.getIdPanier());
        model.addAttribute("idClient", idClientFinal);

        return "client/commandePanier";
    }

    @GetMapping("/details")
    public String voirPanier(
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        Long idClientFinal = resolveClientId(clientId);
        Utilisateur client = utilisateurService.findById(idClientFinal).orElse(null);
        if (client == null) {
            model.addAttribute("error", "Client non trouvé");
            return "client/panier";
        }

        Commande commandePanier = panierService.findPendingCommande(idClientFinal);
        if (commandePanier == null) {
            model.addAttribute("lignes", List.of());
            model.addAttribute("montantTotal", BigDecimal.ZERO);
            return "client/panier";
        }

        List<LigneCommande> lignes = panierService.getLignesByPanierId(1L);

        model.addAttribute("commande", commandePanier);
        model.addAttribute("lignes", lignes);
        model.addAttribute("montantTotal",
                commandePanier.getMontantTotal() == null ? BigDecimal.ZERO : commandePanier.getMontantTotal());
        model.addAttribute("modePaiements", modePaiementService.findAll());

        return "client/commandePanier";
    }

    @PostMapping("/supprimer")
    public String supprimerLigne(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam(value = "clientId", required = false) Long clientId) {
        panierService.supprimerLigne(ligneId, resolveClientId(clientId));
        return "redirect:/client/panier";
    }

    @PostMapping("/quantite")
    public String mettreAJourQuantite(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam("quantite") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId) {
        panierService.mettreAJourQuantite(ligneId, quantite, resolveClientId(clientId));
        return "redirect:/client/panier";
    }

    @PostMapping("/valider")
    public String cloturerPanier(
            @RequestParam("adresseLivraison") String adresseLivraison,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "idPanier", required = false) Long idPanier) {
        Long idClientFinal = resolveClientId(clientId);
        panierService.cloturerPanier(idClientFinal, idPanier, adresseLivraison);
        return "redirect:/client/factures?idClient=" + idClientFinal;
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
                model.addAttribute("error", "Commande introuvable");
                return "client/recu/recap-commande";
            }

            List<LigneCommande> lignes = panierService.getLignesByCommande(commande);
            BigDecimal montantTotal = commande.getMontantTotal() != null
                    ? commande.getMontantTotal() : BigDecimal.ZERO;

            model.addAttribute("commande", commande);
            model.addAttribute("lignes", lignes);
            model.addAttribute("montantTotal", montantTotal);

            return "client/recu/recap-commande";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/recu/recap-commande";
        }
    }
}
