package com.voly_saina.controller.client;

import com.voly_saina.entity.Commande;
import com.voly_saina.entity.LigneCommande;
import com.voly_saina.entity.Produit;
import com.voly_saina.entity.StatutCommande;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.CommandeService;
import com.voly_saina.service.LigneCommandeService;
import com.voly_saina.service.ProduitService;
import com.voly_saina.service.StatutCommandeService;
import com.voly_saina.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import java.math.BigDecimal;

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

    private static final String STATUT_PANIER = "en_attente";
    private static final String STATUT_LIVRAISON = "en_livraison";

    // Ajoute/actualise une ligne dans le panier (1 panier unique par client)
    @GetMapping("/ajouter")
    public String ajouterAuPanier(
            @RequestParam("produitId") Long produitId,
            @RequestParam(value = "quantite", required = false, defaultValue = "1") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model
    ) {
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

            // 1) récupérer/créer la commande panier unique par client
            // (fallback: si on ne trouve pas le statut par code, on crée à la volée celui-ci)
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

            // On utilise la commande client la plus récente qui est en panier (même statut)
            // (faute d'un repository par code/status, on fait un filtre en mémoire)
            // -> ça reste correct pour un MVP. On peut optimiser ensuite.
            Commande commandePanier = commandeService.findAll().stream()
                    .filter(c -> c != null && c.getClient() != null)
                    .filter(c -> c.getClient().getIdUtilisateur() != null && c.getClient().getIdUtilisateur().equals(idClientFinal))
                    .filter(c -> c.getStatutCommande() != null && c.getStatutCommande().getCode() != null)
                    .filter(c -> STATUT_PANIER.equalsIgnoreCase(c.getStatutCommande().getCode()))
                    .sorted((a, b) -> {
                        if (a.getDateCommande() == null && b.getDateCommande() == null) return 0;
                        if (a.getDateCommande() == null) return 1;
                        if (b.getDateCommande() == null) return -1;
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

            // 2) trouver la ligne existante sur ce produit dans ce panier
            LigneCommande ligneExistante = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande() != null && lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                    .filter(lc -> lc.getProduit() != null && lc.getProduit().getIdProduit() != null && lc.getProduit().getIdProduit().equals(produitId))
                    .findFirst()
                    .orElse(null);

            if (ligneExistante != null) {
                BigDecimal nouvelleQuantite = (ligneExistante.getQuantite() == null ? BigDecimal.ZERO : ligneExistante.getQuantite()).add(quantite);
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

            // 3) recalcul du total
            BigDecimal total = ligneCommandeService.findAll().stream()
                    .filter(lc -> lc != null && lc.getCommande() != null)
                    .filter(lc -> lc.getCommande().getIdCommande() != null && lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                    .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            commandePanier.setMontantTotal(total);
            commandeService.save(commandePanier);

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
            Model model
    ) {
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
                .sorted((a, b) -> {
                    if (a.getDateCommande() == null && b.getDateCommande() == null) return 0;
                    if (a.getDateCommande() == null) return 1;
                    if (b.getDateCommande() == null) return -1;
                    return b.getDateCommande().compareTo(a.getDateCommande());
                })
                .findFirst()
                .orElse(null);

        if (commandePanier == null) {
            model.addAttribute("lignes", List.of());
            model.addAttribute("montantTotal", BigDecimal.ZERO);
            return "client/panier";
        }

        var lignes = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null && lc.getCommande().getIdCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                .toList();

        model.addAttribute("commande", commandePanier);
        model.addAttribute("lignes", lignes);
        model.addAttribute("montantTotal", commandePanier.getMontantTotal() == null ? BigDecimal.ZERO : commandePanier.getMontantTotal());
        return "client/panier";
    }

    @org.springframework.web.bind.annotation.PostMapping("/supprimer")
    public String supprimerLigne(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model
    ) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        var ligne = ligneCommandeService.findById(ligneId).orElse(null);
        if (ligne == null) return "redirect:/client/panier";

        var commande = ligne.getCommande();
        if (commande == null || commande.getClient() == null || commande.getClient().getIdUtilisateur() == null || !commande.getClient().getIdUtilisateur().equals(idClientFinal)) {
            return "redirect:/client/panier";
        }

        ligneCommandeService.deleteById(ligneId);

        // recalcul total
        BigDecimal total = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null && lc.getCommande().getIdCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande().equals(commande.getIdCommande()))
                .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commande.setMontantTotal(total);
        commandeService.save(commande);

        return "redirect:/client/panier";
    }

    @org.springframework.web.bind.annotation.PostMapping("/quantite")
    public String mettreAJourQuantite(
            @RequestParam("ligneId") Long ligneId,
            @RequestParam("quantite") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model
    ) {
        Long idClientFinal = clientId != null ? clientId : 1L;
        var ligne = ligneCommandeService.findById(ligneId).orElse(null);
        if (ligne == null) return "redirect:/client/panier";

        var commande = ligne.getCommande();
        if (commande == null || commande.getClient() == null || commande.getClient().getIdUtilisateur() == null || !commande.getClient().getIdUtilisateur().equals(idClientFinal)) {
            return "redirect:/client/panier";
        }

        if (quantite == null || quantite.compareTo(BigDecimal.ONE) < 0) {
            quantite = BigDecimal.ONE;
        }
        ligne.setQuantite(quantite);
        // recalcul sousTotal avec prixUnitaire existant
        if (ligne.getPrixUnitaire() != null) {
            ligne.setSousTotal(ligne.getPrixUnitaire().multiply(quantite));
        }
        ligneCommandeService.save(ligne);

        BigDecimal total = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null && lc.getCommande().getIdCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande().equals(commande.getIdCommande()))
                .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commande.setMontantTotal(total);
        commandeService.save(commande);

        return "redirect:/client/panier";
    }

    @org.springframework.web.bind.annotation.PostMapping("/cloturer")
    public String cloturerPanier(
            @RequestParam("adresseLivraison") String adresseLivraison,
            @RequestParam("modePaiement") String modePaiement,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model
    ) {
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
                    sc.setLibelle("En livraison");
                    return sc;
                });

        commandePanier.setAdresseLivraison(adresseLivraison);
        commandePanier.setModePaiement(modePaiement);
        commandePanier.setStatutCommande(statutLivraison);

        // Sécurité : recalculer le total du panier au moment de la clôture
        BigDecimal total = ligneCommandeService.findAll().stream()
                .filter(lc -> lc != null && lc.getCommande() != null)
                .filter(lc -> lc.getCommande().getIdCommande() != null && lc.getCommande().getIdCommande().equals(commandePanier.getIdCommande()))
                .map(lc -> lc.getSousTotal() == null ? BigDecimal.ZERO : lc.getSousTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        commandePanier.setMontantTotal(total);

        commandeService.save(commandePanier);

        // Message de confirmation côté UI
        model.addAttribute("message", "Achat effectué avec succès. Merci pour votre commande !");

        return "redirect:/client/ventes/catalogue";

    }
}

