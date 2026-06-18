package com.voly_saina.controller;

import com.voly_saina.entity.PretBancaire;
import com.voly_saina.service.PretBancaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prets-bancaires")
public class PretBancaireController {

    @Autowired
    private PretBancaireService pretBancaireService;

    // GET /api/prets-bancaires
    @GetMapping
    public ResponseEntity<List<PretBancaire>> getAll() {
        return ResponseEntity.ok(pretBancaireService.findAll());
    }

    // GET /api/prets-bancaires/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PretBancaire> getById(@PathVariable Long id) {
        return pretBancaireService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/prets-bancaires
    @PostMapping
    public ResponseEntity<PretBancaire> create(@RequestBody PretBancaire pretBancaire) {
        PretBancaire saved = pretBancaireService.save(pretBancaire);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/prets-bancaires/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PretBancaire> update(@PathVariable Long id, @RequestBody PretBancaire pretBancaire) {
        if (!pretBancaireService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        pretBancaire.setIdPret(id);
        PretBancaire updated = pretBancaireService.save(pretBancaire);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/prets-bancaires/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!pretBancaireService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        pretBancaireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
