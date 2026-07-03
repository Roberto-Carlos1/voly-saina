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
import com.voly_saina.entity.ModePaiement;
import com.voly_saina.entity.Produit;
import com.voly_saina.entity.StatutCommande;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.CommandeClientService;
import com.voly_saina.service.CommandeService;
import com.voly_saina.service.LigneCommandeService;
import com.voly_saina.service.ModePaiementService;
import com.voly_saina.service.ProduitService;
import com.voly_saina.service.StatutCommandeService;
import com.voly_saina.service.UtilisateurService;

@Controller
@RequestMapping("/client/panier")
public class PanierController {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private StatutCommandeService statutCommandeService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private LigneCommandeService ligneCommandeService;

    @Autowired
    private CommandeClientService commandeClientService;

    @Autowired
    private ModePaiementService modePaiementService;

    private static final String STATUT_PANIER = "en_attente";
    private static final String STATUT_LIVRAISON = "preparee";

    @GetMapping("/ajouter")
    public String ajouterAuPanier(
            @RequestParam("produitId") Long produitId,
            @RequestParam(value = "quantite", required = false, defaultValue = "1") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        try {
            Long idClientFinal = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClientFinal)
                    .orElseThrow(() -> new RuntimeException("Client non trouvé"));

            Produit produit = produitService.findById(produitId)
                    .orElseThrow(() -> new RuntimeException("Produit introuvable"));

            if (!Boolean.TRUE.equals(produit.getActif())) {
                model.addAttribute("error", "Produit indisponible");
                return "client/ventes/detail";
            }

            if (quantite == null || quantite.compareTo(BigDecimal.ONE) < 0) {
                quantite = BigDecimal.ONE;
            }

            StatutCommande statutPanier = statutCommandeService.findAll().stream()
                    .filter(sc -> sc != null && STATUT_PANIER.equalsIgnoreCase(sc.getCode()))
                    .findFirst()
                    .orElseGet(() -> {
                        StatutCommande sc = new StatutCommande();
                        sc.setIdStatutCommande(0L);
                        sc.setCode(STATUT_PANIER);
                        sc.setLibelle("En attente");
                        return sc;
                    });

            Commande commandePanier = getOrCreatePanier(client, idClientFinal, statutPanier);

            LigneCommande ligneExistante = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande() != null
                            && lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                    .filter(lc -> lc.getProduit() != null && lc.getProduit().getIdProduit() != null
                            && lc.getProduit().getIdProduit().equals(produitId))
                    .findFirst()
                    .orElse(null);

            if (ligneExistante != null) {
                BigDecimal nouvelleQuantite = (ligneExistante.getQuantite() == null ? BigDecimal.ZERO
                        : ligneExistante.getQuantite()).add(quantite);
                ligneExistante.setQuantite(nouvelleQuantite);
                ligneExistante.setPrixUnitaire(produit.getPrixUnitaire());
                ligneExistante.setSousTotal(produit.getPrixUnitaire().multiply(nouvelleQuantite));
                ligneCommandeService.save(ligneExistante);
            } else {
                LigneCommande ligne = new LigneCommande();
                ligne.setCommande(commandePanier);
                ligne.setProduit(produit);
                ligne.setQuantite(quantite);
                ligne.setPrixUnitaire(produit.getPrixUnitaire());
                ligne.setSousTotal(produit.getPrixUnitaire().multiply(quantite));
                ligneCommandeService.save(ligne);
            }

            updatePanierTotal(commandePanier);

            return "redirect:/client/panier";

        } catch (Exception e) {
            model.addAttribute("produit", produitService.findById(produitId).orElse(null));
            model.addAttribute("error", e.getMessage());
            return "client/ventes/detail";
        }
    }

    @GetMapping
    public String voirPanier(
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        Utilisateur client = utilisateurService.findById(idClientFinal).orElse(null);
        if (client == null) {
            model.addAttribute("error", "Client non trouvé");
            return "client/panier";
        }

        Commande commandePanier = commandeService.findAll().stream()
                .filter(c -> c != null && c.getClient() != null)
                .filter(c -> c.getClient().getIdUtilisateur().equals(idClientFinal))
                .filter(c -> c.getStatutCommande() != null && "en_attente".equalsIgnoreCase(c.getStatutCommande().getCode()))
                .findFirst()
                .orElse(null);

        List<LigneCommande> lignes = List.of();
        BigDecimal totalProduits = BigDecimal.ZERO;

        if (commandePanier != null) {
            lignes = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                    .toList();

            totalProduits = lignes.stream()
                    .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        model.addAttribute("commande", commandePanier);
        model.addAttribute("lignes", lignes);
        model.addAttribute("totalProduits", totalProduits);
        model.addAttribute("montantTotal", totalProduits);

        List<ModePaiement> modePaiements = modePaiementService.findAll();
        model.addAttribute("modePaiements", modePaiements);

        return "client/panier";
    }

    @PostMapping("/supprimer")
    public String supprimerLigne(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        var ligne = ligneCommandeService.findById(ligneId).orElse(null);
        if (ligne == null)
            return "redirect:/client/panier";

        var commande = ligne.getCommande();
        if (commande == null || commande.getClient() == null || commande.getClient().getIdUtilisateur() == null
                || !commande.getClient().getIdUtilisateur().equals(idClientFinal)) {
            return "redirect:/client/panier";
        }

        ligneCommandeService.deleteById(ligneId);
        updatePanierTotal(commande);

        return "redirect:/client/panier";
    }

    @PostMapping("/quantite")
    public String mettreAJourQuantite(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam("quantite") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        var ligne = ligneCommandeService.findById(ligneId).orElse(null);
        if (ligne == null)
            return "redirect:/client/panier";

        var commande = ligne.getCommande();
        if (commande == null || commande.getClient() == null || commande.getClient().getIdUtilisateur() == null
                || !commande.getClient().getIdUtilisateur().equals(idClientFinal)) {
            return "redirect:/client/panier";
        }

        if (quantite == null || quantite.compareTo(BigDecimal.ONE) < 0) {
            quantite = BigDecimal.ONE;
        }
        ligne.setQuantite(quantite);
        if (ligne.getPrixUnitaire() != null) {
            ligne.setSousTotal(ligne.getPrixUnitaire().multiply(quantite));
        }
        ligneCommandeService.save(ligne);
        updatePanierTotal(commande);

        return "redirect:/client/panier";
    }

    @PostMapping("/cloturer")
    public String cloturerPanier(
            @RequestParam("adresseLivraison") String adresseLivraison,
            @RequestParam("modePaiement") Long modePaiement,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model) {
        try {
            Long idClientFinal = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClientFinal).orElse(null);
            if (client == null) {
                model.addAttribute("error", "Client non trouvé");
                return "client/panier";
            }

            Commande commandePanier = commandeService.findAll().stream()
                    .filter(c -> c != null && c.getClient() != null && c.getClient().getIdUtilisateur() != null)
                    .filter(c -> c.getClient().getIdUtilisateur().equals(idClientFinal))
                    .filter(c -> c.getStatutCommande() != null && c.getStatutCommande().getCode() != null)
                    .filter(c -> STATUT_PANIER.equalsIgnoreCase(c.getStatutCommande().getCode()))
                    .findFirst()
                    .orElse(null);

            if (commandePanier == null) {
                model.addAttribute("error", "Panier introuvable");
                return "client/panier";
            }

            StatutCommande statutLivraison = statutCommandeService.findAll().stream()
                    .filter(sc -> sc != null && STATUT_LIVRAISON.equalsIgnoreCase(sc.getCode()))
                    .findFirst()
                    .orElseGet(() -> {
                        StatutCommande sc = new StatutCommande();
                        sc.setIdStatutCommande(0L);
                        sc.setCode(STATUT_LIVRAISON);
                        sc.setLibelle("Préparée");
                        return sc;
                    });

            commandePanier.setAdresseLivraison(adresseLivraison);
            ModePaiement mode = modePaiementService.findById(modePaiement);
            commandePanier.setModePaiement(mode);
            commandePanier.setStatutCommande(statutLivraison);

            var lignes = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null && lc.getCommande().getIdCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                    .toList();

            for (LigneCommande lc : lignes) {
                if (lc == null)
                    continue;
                BigDecimal q = lc.getQuantite() == null ? BigDecimal.ZERO : lc.getQuantite();
                BigDecimal p = lc.getPrixUnitaire() == null ? BigDecimal.ZERO : lc.getPrixUnitaire();
                lc.setSousTotal(p.multiply(q));
                ligneCommandeService.save(lc);
            }

            BigDecimal total = commandeClientService.calculerMontant(commandePanier, lignes);
            commandePanier.setMontantTotal(total);
            commandeService.save(commandePanier);

            commandeClientService.creerOperation("commande", commandePanier, lignes, null);

            return "redirect:/client/panier/recap?commandeId=" + commandePanier.getIdCommande();
        } catch (Exception e) {
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
            Long idClientFinal = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClientFinal).orElse(null);
            if (client == null) {
                model.addAttribute("error", "Client non trouvé");
                return "client/recu/recap-commande";
            }

            Commande commande = commandeService.findAll().stream()
                    .filter(c -> c != null && c.getIdCommande() != null)
                    .filter(c -> c.getIdCommande().equals(commandeId))
                    .findFirst()
                    .orElse(null);

            if (commande == null || commande.getClient() == null || commande.getClient().getIdUtilisateur() == null) {
                model.addAttribute("error", "Commande introuvable");
                return "client/recu/recap-commande";
            }

            if (!commande.getClient().getIdUtilisateur().equals(idClientFinal)) {
                model.addAttribute("error", "Accès refusé");
                return "client/recu/recap-commande";
            }

            var lignes = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande().equals(commandeId))
                    .toList();

            BigDecimal montantTotal = commande.getMontantTotal() != null ? commande.getMontantTotal() : BigDecimal.ZERO;

            model.addAttribute("commande", commande);
            model.addAttribute("lignes", lignes);
            model.addAttribute("montantTotal", montantTotal);

            return "client/recu/recap-commande";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/recu/recap-commande";
        }
    }

    private Commande getOrCreatePanier(Utilisateur client, Long idClientFinal, StatutCommande statutPanier) {
        return commandeService.findAll().stream()
                .filter(c -> c != null && c.getClient() != null)
                .filter(c -> c.getClient().getIdUtilisateur() != null
                        && c.getClient().getIdUtilisateur().equals(idClientFinal))
                .filter(c -> c.getStatutCommande() != null && c.getStatutCommande().getCode() != null)
                .filter(c -> STATUT_PANIER.equalsIgnoreCase(c.getStatutCommande().getCode()))
                .sorted((a, b) -> {
                    if (a.getDateCommande() == null && b.getDateCommande() == null)
                        return 0;
                    if (a.getDateCommande() == null)
                        return 1;
                    if (b.getDateCommande() == null)
                        return -1;
                    return b.getDateCommande().compareTo(a.getDateCommande());
                })
                .findFirst()
                .orElseGet(() -> {
                    Commande c = new Commande();
                    c.setClient(client);
                    c.setStatutCommande(statutPanier);
                    c.setMontantTotal(BigDecimal.ZERO);
                    return commandeService.save(c);
                });
    }

    private void updatePanierTotal(Commande commande) {
        BigDecimal total = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande() != null
                        && lc.getCommande().getIdCommande().equals(commande.getIdCommande()))
                .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commande.setMontantTotal(total);
        commandeService.save(commande);
    }
}