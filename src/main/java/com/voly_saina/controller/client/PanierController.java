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

    // Ajout d'un produit au panier (commande en cours)
    // NOTE: pas de panier UX encore; on redirige sur la page produit.
    @GetMapping("/ajouter")
    public String ajouterAuPanier(
            @RequestParam("produitId") Long produitId,
            @RequestParam(value = "quantite", required = false, defaultValue = "1") BigDecimal quantite,
            @RequestParam(value = "clientId", required = false) Long clientId,
            Model model
    ) {
        try {
            Long idClientFinal = clientId != null ? clientId : 1L; // fallback projet actuel (ex. autres controllers)
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

            StatutCommande statutEnAttente = statutCommandeService
                    .findById(1L)
                    .orElseGet(() -> {
                        // fallback: si le statut ID 1 n'existe pas, on laisse passer (création commande)
                        StatutCommande sc = new StatutCommande();
                        sc.setIdStatutCommande(1L);
                        sc.setCode("en_attente");
                        sc.setLibelle("En attente");
                        return sc;
                    });

            // Crée directement une commande “en attente” (pas encore de panier unique par client dans le code actuel)
            Commande commande = new Commande();
            commande.setClient(client);
            commande.setStatutCommande(statutEnAttente);
            commande.setMontantTotal(BigDecimal.ZERO);
            Commande savedCommande = commandeService.save(commande);

            LigneCommande ligne = new LigneCommande();
            ligne.setCommande(savedCommande);
            ligne.setProduit(produit);
            ligne.setQuantite(quantite);
            ligne.setPrixUnitaire(produit.getPrixUnitaire());
            BigDecimal sousTotal = produit.getPrixUnitaire().multiply(quantite);
            ligne.setSousTotal(sousTotal);
            ligneCommandeService.save(ligne);

            model.addAttribute("produit", produit);
            model.addAttribute("estDisponible", true);
            return "redirect:/client/ventes/" + produitId;

        } catch (Exception e) {
            model.addAttribute("produit", produitService.findById(produitId).orElse(null));
            model.addAttribute("error", e.getMessage());
            return "client/ventes/detail";
        }
    }
}

