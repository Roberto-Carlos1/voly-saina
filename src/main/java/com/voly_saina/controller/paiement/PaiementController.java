package com.voly_saina.controller.paiement;

import com.voly_saina.entity.Paiement;
import com.voly_saina.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    // GET /api/paiements
    @GetMapping
    public ResponseEntity<List<Paiement>> getAll() {
        return ResponseEntity.ok(paiementService.findAll());
    }

    // GET /api/paiements/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Paiement> getById(@PathVariable Long id) {
        return paiementService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/paiements
    @PostMapping
    public ResponseEntity<Paiement> create(@RequestBody Paiement paiement) {
        Paiement saved = paiementService.save(paiement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/paiements/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Paiement> update(@PathVariable Long id, @RequestBody Paiement paiement) {
        if (!paiementService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        paiement.setIdPaiement(id);
        Paiement updated = paiementService.save(paiement);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/paiements/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!paiementService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        paiementService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
