package com.voly_saina.controller;

import com.voly_saina.entity.LigneCommande;
import com.voly_saina.service.LigneCommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lignes-commande")
public class LigneCommandeController {

    @Autowired
    private LigneCommandeService ligneCommandeService;

    // GET /api/lignes-commande
    @GetMapping
    public ResponseEntity<List<LigneCommande>> getAll() {
        return ResponseEntity.ok(ligneCommandeService.findAll());
    }

    // GET /api/lignes-commande/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LigneCommande> getById(@PathVariable Long id) {
        return ligneCommandeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/lignes-commande
    @PostMapping
    public ResponseEntity<LigneCommande> create(@RequestBody LigneCommande ligneCommande) {
        LigneCommande saved = ligneCommandeService.save(ligneCommande);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/lignes-commande/{id}
    @PutMapping("/{id}")
    public ResponseEntity<LigneCommande> update(@PathVariable Long id, @RequestBody LigneCommande ligneCommande) {
        if (!ligneCommandeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ligneCommande.setIdLigne(id);
        LigneCommande updated = ligneCommandeService.save(ligneCommande);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/lignes-commande/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!ligneCommandeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ligneCommandeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
