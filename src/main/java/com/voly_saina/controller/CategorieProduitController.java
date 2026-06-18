package com.voly_saina.controller;

import com.voly_saina.entity.CategorieProduit;
import com.voly_saina.service.CategorieProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories-produit")
public class CategorieProduitController {

    @Autowired
    private CategorieProduitService categorieProduitService;

    // GET /api/categories-produit
    @GetMapping
    public ResponseEntity<List<CategorieProduit>> getAll() {
        return ResponseEntity.ok(categorieProduitService.findAll());
    }

    // GET /api/categories-produit/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CategorieProduit> getById(@PathVariable Long id) {
        return categorieProduitService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/categories-produit
    @PostMapping
    public ResponseEntity<CategorieProduit> create(@RequestBody CategorieProduit categorieProduit) {
        CategorieProduit saved = categorieProduitService.save(categorieProduit);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/categories-produit/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CategorieProduit> update(@PathVariable Long id, @RequestBody CategorieProduit categorieProduit) {
        if (!categorieProduitService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        categorieProduit.setIdCategorie(id);
        CategorieProduit updated = categorieProduitService.save(categorieProduit);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/categories-produit/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!categorieProduitService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        categorieProduitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
