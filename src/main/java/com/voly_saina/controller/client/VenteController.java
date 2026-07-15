package com.voly_saina.controller.client;

import com.voly_saina.entity.ModePaiement;
import com.voly_saina.entity.Produit;
import com.voly_saina.service.ProduitService;
import com.voly_saina.service.ProduitImageService;
import com.voly_saina.service.CategorieProduitService;
import com.voly_saina.service.ModePaiementService;

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
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalogue/produits")
public class VenteController {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private CategorieProduitService categorieProduitService;

    @Autowired
    private ModePaiementService modePaiementService;

    @Autowired
    private ProduitImageService produitImageService;

    // ==================== CATALOGUE (HTML) ====================

    // GET /catalogue/produits/catalogue
    @GetMapping("/catalogue")
    public String catalogue(
            Model model,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categorie", required = false) String categorie,
            @RequestParam(value = "disponible", required = false) String disponible,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        List<Produit> produits = produitService.findAll();
        model.addAttribute("categories", categorieProduitService.findAll());


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
                        String categorieNom = (p.getCategorie() != null && p.getCategorie().getNom() != null) ? p.getCategorie().getNom().toLowerCase(): "";
                        boolean matchQ = nom.contains(qNorm) || description.contains(qNorm) || conseil.contains(qNorm) || categorieNom.contains(qNorm);
                        if (!matchQ) return false;
                    }
                    return true;
                })
                .filter(p -> {
                    if (!categorieNorm.isEmpty()) {
                        String categorieNom = (p.getCategorie() != null && p.getCategorie().getNom() != null) ? p.getCategorie().getNom().toLowerCase(): "";
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

        int total = filtres.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<Produit> pageProduits = filtres.subList(fromIndex, toIndex);

        // Pour compatibilité avec le template: la liste 'produits' contient déjà les filtres
        model.addAttribute("produits", pageProduits);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", size > 0 ? (int) Math.ceil((double) total / (double) size) : 1);

        model.addAttribute("q", q);
        model.addAttribute("categorie", categorie);
        model.addAttribute("disponible", disponible);

        // Image mapping for product cards
        Map<Long, String> imageMap = pageProduits.stream()
                .filter(p -> p.getIdProduit() != null)
                .collect(Collectors.toMap(
                        com.voly_saina.entity.Produit::getIdProduit,
                        p -> produitImageService.getImagePath(p.getIdProduit())
                ));
        model.addAttribute("produitImages", imageMap);

        return "client/ventes/catalogue";
    }

    // GET /catalogue/produits/disponibles
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

    // GET /catalogue/produits/{id}
    @GetMapping("/{id}")
    public String detailProduit(@PathVariable Long id, Model model) {
        Produit produit = produitService.findById(id).orElse(null);
        if (produit == null || !Boolean.TRUE.equals(produit.getActif())) {
            return "Désolé, le produit demandé n'est pas disponible.";
        }

        model.addAttribute("produit", produit);
        model.addAttribute("estDisponible", estDisponible(produit));
        model.addAttribute("produitImage", produitImageService.getImagePath(produit.getIdProduit()));
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

    // ==================== API JSON (CATALOGUE) ====================

    // GET /catalogue/produits/api/catalogue
    @GetMapping("/api/catalogue")
    @ResponseBody
    public ResponseEntity<List<com.voly_saina.dto.CatalogueProduitDTO>> apiCatalogue(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categorie", required = false) String categorie,
            @RequestParam(value = "disponible", required = false) String disponible
    ) {
        List<Produit> produits = produitService.findAll();

        String qNorm = (q == null) ? "" : q.trim().toLowerCase();
        String categorieNorm = (categorie == null) ? "" : categorie.trim().toLowerCase();

        List<com.voly_saina.dto.CatalogueProduitDTO> result = produits.stream()
                .filter(p -> p != null)
                .filter(Produit::getActif)
                .filter(p -> {
                    if (!qNorm.isEmpty()) {
                        String nom = p.getNom() != null ? p.getNom().toLowerCase() : "";
                        String description = p.getDescription() != null ? p.getDescription().toLowerCase() : "";
                        String conseil = p.getConseilUsage() != null ? p.getConseilUsage().toLowerCase() : "";
                        String categorieNom = (p.getCategorie() != null && p.getCategorie().getNom() != null) ? p.getCategorie().getNom().toLowerCase() : "";

                        return nom.contains(qNorm) || description.contains(qNorm) || conseil.contains(qNorm) || categorieNom.contains(qNorm);
                    }
                    return true;
                })
                .filter(p -> {
                    if (!categorieNorm.isEmpty()) {
                        String categorieNom = (p.getCategorie() != null && p.getCategorie().getNom() != null)
                                ? p.getCategorie().getNom().toLowerCase() : "";
                        return categorieNom.contains(categorieNorm);
                    }
                    return true;
                })
                .filter(p -> {
                    if (disponible == null || disponible.isBlank()) return true;
                    boolean estDispo = estDisponible(p);
                    return "oui".equalsIgnoreCase(disponible)
                            ? estDispo
                            : "non".equalsIgnoreCase(disponible)
                                ? !estDispo
                                : true;
                })
                .map(p -> {
                    com.voly_saina.dto.CatalogueProduitDTO dto = new com.voly_saina.dto.CatalogueProduitDTO();
                    dto.setIdProduit(p.getIdProduit());
                    dto.setNom(p.getNom());
                    dto.setDescription(p.getDescription());
                    dto.setStock(p.getStock());
                    dto.setDateExpiration(p.getDateExpiration());
                    dto.setActif(Boolean.TRUE.equals(p.getActif()));
                    dto.setDisponible(estDisponible(p));
                    dto.setCategorie(p.getCategorie());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    // GET /catalogue/produits/api/produits
    @GetMapping("/api/produits")
    @ResponseBody
    public ResponseEntity<List<Produit>> apiListeProduits() {
        return ResponseEntity.ok(produitService.findAll());
    }


    // GET /catalogue/produits/api/produits/{id}
    @GetMapping("/api/produits/{id}")
    @ResponseBody
    public ResponseEntity<Produit> apiDetailProduit(@PathVariable Long id) {
        return produitService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // GET /catalogue/produits/api/produits/disponibles
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

