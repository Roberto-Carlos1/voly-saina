package com.voly_saina.controller;

import com.voly_saina.entity.StatutPret;
import com.voly_saina.service.StatutPretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts-pret")
public class StatutPretController {

    @Autowired
    private StatutPretService statutPretService;

    // GET /api/statuts-pret
    @GetMapping
    public ResponseEntity<List<StatutPret>> getAll() {
        return ResponseEntity.ok(statutPretService.findAll());
    }

    // GET /api/statuts-pret/{id}
    @GetMapping("/{id}")
    public ResponseEntity<StatutPret> getById(@PathVariable Long id) {
        return statutPretService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/statuts-pret
    @PostMapping
    public ResponseEntity<StatutPret> create(@RequestBody StatutPret statutPret) {
        StatutPret saved = statutPretService.save(statutPret);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/statuts-pret/{id}
    @PutMapping("/{id}")
    public ResponseEntity<StatutPret> update(@PathVariable Long id, @RequestBody StatutPret statutPret) {
        if (!statutPretService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutPret.setIdStatutPret(id);
        StatutPret updated = statutPretService.save(statutPret);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/statuts-pret/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!statutPretService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutPretService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
