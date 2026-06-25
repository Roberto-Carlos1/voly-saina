package com.voly_saina.controller.client;

import com.voly_saina.entity.Produit;
import com.voly_saina.service.ProduitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/client/ventes")
public class VenteController {

    @Autowired
    private ProduitService produitService;

    // ==================== CATALOGUE (HTML) ====================

    // GET /client/ventes/catalogue
    @GetMapping("/catalogue")
    public String catalogue(
            Model model,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categorie", required = false) String categorie,
            @RequestParam(value = "disponible", required = false) String disponible
    ) {
        List<Produit> produits = produitService.findAll();

        String qNorm = (q == null) ? "" : q.trim().toLowerCase();
        String categorieNorm = (categorie == null) ? "" : categorie.trim().toLowerCase();

        LocalDate today = LocalDate.now();

        List<Produit> filtres = produits.stream()
                .filter(p -> p != null)
                .filter(Produit::getActif)
                .filter(p -> {
                    if (!qNorm.isEmpty()) {
                        String nom = p.getNom() != null ? p.getNom().toLowerCase() : "";
                        String description = p.getDescription() != null ? p.getDescription().toLowerCase() : "";
                        String conseil = p.getConseilUsage() != null ? p.getConseilUsage().toLowerCase() : "";
                        String categorieNom = (p.getCategorie() != null && p.getCategorie().getNom() != null)
                                ? p.getCategorie().getNom().toLowerCase()
                                : "";

                        boolean matchQ = nom.contains(qNorm) || description.contains(qNorm) || conseil.contains(qNorm) || categorieNom.contains(qNorm);
                        if (!matchQ) return false;
                    }
                    return true;
                })
                .filter(p -> {
                    if (!categorieNorm.isEmpty()) {
                        String categorieNom = (p.getCategorie() != null && p.getCategorie().getNom() != null)
                                ? p.getCategorie().getNom().toLowerCase()
                                : "";
                        if (!categorieNom.contains(categorieNorm)) return false;
                    }
                    return true;
                })
                .filter(p -> {
                    if (disponible == null || disponible.isBlank()) {
                        return true;
                    }
                    boolean estDispo = estDisponible(p);
                    return "oui".equalsIgnoreCase(disponible) ? estDispo : "non".equalsIgnoreCase(disponible) ? !estDispo : true;
                })
                .toList();


        // Pour compatibilité avec le template: la liste 'produits' contient déjà les filtres
        model.addAttribute("produits", filtres);
        model.addAttribute("q", q);
        model.addAttribute("categorie", categorie);
        model.addAttribute("disponible", disponible);
        return "client/ventes/catalogue";
    }

    // GET /client/ventes/disponibles
    @GetMapping("/disponibles")
    public String produitsDisponibles(Model model) {
        List<Produit> produits = produitService.findAll();
        LocalDate today = LocalDate.now();

        List<Produit> disponibles = produits.stream()
                .filter(Produit::getActif)
                .filter(p -> p.getStock() != null && p.getStock().compareTo(p.getSeuilStock()) > 0)
                .filter(p -> p.getDateExpiration() == null || p.getDateExpiration().isAfter(today))
                .toList();

        model.addAttribute("produits", disponibles);
        return "client/ventes/disponibles";
    }

    // GET /client/ventes/{id}
    @GetMapping("/{id}")
    public String detailProduit(@PathVariable Long id, Model model) {
        Produit produit = produitService.findById(id).orElse(null);
        if (produit == null || !Boolean.TRUE.equals(produit.getActif())) {
            return "Désolé, le produit demandé n'est pas disponible.";
        }

        model.addAttribute("produit", produit);
        model.addAttribute("estDisponible", estDisponible(produit));
        return "client/ventes/detail";
    }

    private boolean estDisponible(Produit produit) {
        if (produit == null) return false;
        if (!Boolean.TRUE.equals(produit.getActif())) return false;
        if (produit.getStock() == null || produit.getSeuilStock() == null) return false;
        if (produit.getStock().compareTo(produit.getSeuilStock()) <= 0) return false;
        if (produit.getDateExpiration() != null && !produit.getDateExpiration().isAfter(LocalDate.now())) return false;
        return true;
    }

    // ==================== API JSON ====================

    // GET /client/ventes/api/produits
    @GetMapping("/api/produits")
    @ResponseBody
    public ResponseEntity<List<Produit>> apiListeProduits() {
        return ResponseEntity.ok(produitService.findAll());
    }

    // GET /client/ventes/api/produits/{id}
    @GetMapping("/api/produits/{id}")
    @ResponseBody
    public ResponseEntity<Produit> apiDetailProduit(@PathVariable Long id) {
        return produitService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // GET /client/ventes/api/produits/disponibles
    @GetMapping("/api/produits/disponibles")
    @ResponseBody
    public ResponseEntity<List<Produit>> apiProduitsDisponibles() {
        LocalDate today = LocalDate.now();
        List<Produit> disponibles = produitService.findAll().stream()
                .filter(Produit::getActif)
                .filter(p -> p.getStock() != null && p.getStock().compareTo(p.getSeuilStock()) > 0)
                .filter(p -> p.getDateExpiration() == null || p.getDateExpiration().isAfter(today))
                .toList();

        return ResponseEntity.ok(disponibles);
    }
}

