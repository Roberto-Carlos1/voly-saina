package com.voly_saina.controller;

import com.voly_saina.entity.Rapport;
import com.voly_saina.service.RapportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports")
public class RapportController {

    @Autowired
    private RapportService rapportService;

    // GET /api/rapports
    @GetMapping
    public ResponseEntity<List<Rapport>> getAll() {
        return ResponseEntity.ok(rapportService.findAll());
    }

    // GET /api/rapports/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Rapport> getById(@PathVariable Long id) {
        return rapportService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/rapports
    @PostMapping
    public ResponseEntity<Rapport> create(@RequestBody Rapport rapport) {
        Rapport saved = rapportService.save(rapport);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/rapports/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Rapport> update(@PathVariable Long id, @RequestBody Rapport rapport) {
        if (!rapportService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        rapport.setIdRapport(id);
        Rapport updated = rapportService.save(rapport);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/rapports/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!rapportService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        rapportService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
