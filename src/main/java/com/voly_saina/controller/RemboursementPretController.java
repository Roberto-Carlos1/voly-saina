package com.voly_saina.controller;

import com.voly_saina.entity.RemboursementPret;
import com.voly_saina.service.RemboursementPretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/remboursements-pret")
public class RemboursementPretController {

    @Autowired
    private RemboursementPretService remboursementPretService;

    // GET /api/remboursements-pret
    @GetMapping
    public ResponseEntity<List<RemboursementPret>> getAll() {
        return ResponseEntity.ok(remboursementPretService.findAll());
    }

    // GET /api/remboursements-pret/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RemboursementPret> getById(@PathVariable Long id) {
        return remboursementPretService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/remboursements-pret
    @PostMapping
    public ResponseEntity<RemboursementPret> create(@RequestBody RemboursementPret remboursementPret) {
        RemboursementPret saved = remboursementPretService.save(remboursementPret);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/remboursements-pret/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RemboursementPret> update(@PathVariable Long id, @RequestBody RemboursementPret remboursementPret) {
        if (!remboursementPretService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        remboursementPret.setIdRemboursement(id);
        RemboursementPret updated = remboursementPretService.save(remboursementPret);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/remboursements-pret/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!remboursementPretService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        remboursementPretService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
