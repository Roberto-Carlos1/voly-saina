package com.voly_saina.controller;

import com.voly_saina.entity.StatutTache;
import com.voly_saina.service.StatutTacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts-tache")
public class StatutTacheController {

    @Autowired
    private StatutTacheService statutTacheService;

    // GET /api/statuts-tache
    @GetMapping
    public ResponseEntity<List<StatutTache>> getAll() {
        return ResponseEntity.ok(statutTacheService.findAll());
    }

    // GET /api/statuts-tache/{id}
    @GetMapping("/{id}")
    public ResponseEntity<StatutTache> getById(@PathVariable Long id) {
        return statutTacheService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/statuts-tache
    @PostMapping
    public ResponseEntity<StatutTache> create(@RequestBody StatutTache statutTache) {
        StatutTache saved = statutTacheService.save(statutTache);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/statuts-tache/{id}
    @PutMapping("/{id}")
    public ResponseEntity<StatutTache> update(@PathVariable Long id, @RequestBody StatutTache statutTache) {
        if (!statutTacheService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutTache.setIdStatutTache(id);
        StatutTache updated = statutTacheService.save(statutTache);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/statuts-tache/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!statutTacheService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutTacheService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
