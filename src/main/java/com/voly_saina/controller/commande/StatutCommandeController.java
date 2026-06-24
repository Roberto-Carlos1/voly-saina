package com.voly_saina.controller.commande;

import com.voly_saina.entity.StatutCommande;
import com.voly_saina.service.StatutCommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts-commande")
public class StatutCommandeController {

    @Autowired
    private StatutCommandeService statutCommandeService;

    // GET /api/statuts-commande
    @GetMapping
    public ResponseEntity<List<StatutCommande>> getAll() {
        return ResponseEntity.ok(statutCommandeService.findAll());
    }

    // GET /api/statuts-commande/{id}
    @GetMapping("/{id}")
    public ResponseEntity<StatutCommande> getById(@PathVariable Long id) {
        return statutCommandeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/statuts-commande
    @PostMapping
    public ResponseEntity<StatutCommande> create(@RequestBody StatutCommande statutCommande) {
        StatutCommande saved = statutCommandeService.save(statutCommande);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/statuts-commande/{id}
    @PutMapping("/{id}")
    public ResponseEntity<StatutCommande> update(@PathVariable Long id, @RequestBody StatutCommande statutCommande) {
        if (!statutCommandeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutCommande.setIdStatutCommande(id);
        StatutCommande updated = statutCommandeService.save(statutCommande);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/statuts-commande/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!statutCommandeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutCommandeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
