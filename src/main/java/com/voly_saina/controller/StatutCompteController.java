package com.voly_saina.controller;

import com.voly_saina.entity.StatutCompte;
import com.voly_saina.service.StatutCompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts-compte")
public class StatutCompteController {

    @Autowired
    private StatutCompteService statutCompteService;

    // GET /api/statuts-compte
    @GetMapping
    public ResponseEntity<List<StatutCompte>> getAll() {
        return ResponseEntity.ok(statutCompteService.findAll());
    }

    // GET /api/statuts-compte/{id}
    @GetMapping("/{id}")
    public ResponseEntity<StatutCompte> getById(@PathVariable Long id) {
        return statutCompteService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/statuts-compte
    @PostMapping
    public ResponseEntity<StatutCompte> create(@RequestBody StatutCompte statutCompte) {
        StatutCompte saved = statutCompteService.save(statutCompte);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/statuts-compte/{id}
    @PutMapping("/{id}")
    public ResponseEntity<StatutCompte> update(@PathVariable Long id, @RequestBody StatutCompte statutCompte) {
        if (!statutCompteService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutCompte.setIdStatutCompte(id);
        StatutCompte updated = statutCompteService.save(statutCompte);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/statuts-compte/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!statutCompteService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutCompteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
