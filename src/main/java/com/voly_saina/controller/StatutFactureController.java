package com.voly_saina.controller;

import com.voly_saina.entity.StatutFacture;
import com.voly_saina.service.StatutFactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts-facture")
public class StatutFactureController {

    @Autowired
    private StatutFactureService statutFactureService;

    // GET /api/statuts-facture
    @GetMapping
    public ResponseEntity<List<StatutFacture>> getAll() {
        return ResponseEntity.ok(statutFactureService.findAll());
    }

    // GET /api/statuts-facture/{id}
    @GetMapping("/{id}")
    public ResponseEntity<StatutFacture> getById(@PathVariable Long id) {
        return statutFactureService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/statuts-facture
    @PostMapping
    public ResponseEntity<StatutFacture> create(@RequestBody StatutFacture statutFacture) {
        StatutFacture saved = statutFactureService.save(statutFacture);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/statuts-facture/{id}
    @PutMapping("/{id}")
    public ResponseEntity<StatutFacture> update(@PathVariable Long id, @RequestBody StatutFacture statutFacture) {
        if (!statutFactureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutFacture.setIdStatutFacture(id);
        StatutFacture updated = statutFactureService.save(statutFacture);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/statuts-facture/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!statutFactureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutFactureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
