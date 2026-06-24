package com.voly_saina.controller.stock;

import com.voly_saina.entity.Produit;
import com.voly_saina.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    // GET /api/produits
    @GetMapping
    public ResponseEntity<List<Produit>> getAll() {
        return ResponseEntity.ok(produitService.findAll());
    }

    // GET /api/produits/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Produit> getById(@PathVariable Long id) {
        return produitService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/produits
    @PostMapping
    public ResponseEntity<Produit> create(@RequestBody Produit produit) {
        Produit saved = produitService.save(produit);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/produits/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Produit> update(@PathVariable Long id, @RequestBody Produit produit) {
        if (!produitService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        produit.setIdProduit(id);
        Produit updated = produitService.save(produit);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/produits/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!produitService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        produitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
