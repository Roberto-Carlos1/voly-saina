package com.voly_saina.controller.paiement;

import com.voly_saina.entity.Facture;
import com.voly_saina.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    @Autowired
    private FactureService factureService;

    // GET /api/factures
    @GetMapping
    public ResponseEntity<List<Facture>> getAll() {
        return ResponseEntity.ok(factureService.findAll());
    }

    // GET /api/factures/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Facture> getById(@PathVariable Long id) {
        return factureService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/factures
    @PostMapping
    public ResponseEntity<Facture> create(@RequestBody Facture facture) {
        Facture saved = factureService.save(facture);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/factures/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Facture> update(@PathVariable Long id, @RequestBody Facture facture) {
        if (!factureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        facture.setIdFacture(id);
        Facture updated = factureService.save(facture);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/factures/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!factureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        factureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
