package com.voly_saina.controller;

import com.voly_saina.entity.Commande;
import com.voly_saina.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    @Autowired
    private CommandeService commandeService;

    // GET /api/commandes
    @GetMapping
    public ResponseEntity<List<Commande>> getAll() {
        return ResponseEntity.ok(commandeService.findAll());
    }

    // GET /api/commandes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Commande> getById(@PathVariable Long id) {
        return commandeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/commandes
    @PostMapping
    public ResponseEntity<Commande> create(@RequestBody Commande commande) {
        Commande saved = commandeService.save(commande);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/commandes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Commande> update(@PathVariable Long id, @RequestBody Commande commande) {
        if (!commandeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        commande.setIdCommande(id);
        Commande updated = commandeService.save(commande);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/commandes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!commandeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        commandeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
