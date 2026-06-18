package com.voly_saina.controller;

import com.voly_saina.entity.MouvementStock;
import com.voly_saina.service.MouvementStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements-stock")
public class MouvementStockController {

    @Autowired
    private MouvementStockService mouvementStockService;

    // GET /api/mouvements-stock
    @GetMapping
    public ResponseEntity<List<MouvementStock>> getAll() {
        return ResponseEntity.ok(mouvementStockService.findAll());
    }

    // GET /api/mouvements-stock/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MouvementStock> getById(@PathVariable Long id) {
        return mouvementStockService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/mouvements-stock
    @PostMapping
    public ResponseEntity<MouvementStock> create(@RequestBody MouvementStock mouvementStock) {
        MouvementStock saved = mouvementStockService.save(mouvementStock);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/mouvements-stock/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MouvementStock> update(@PathVariable Long id, @RequestBody MouvementStock mouvementStock) {
        if (!mouvementStockService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        mouvementStock.setIdMouvement(id);
        MouvementStock updated = mouvementStockService.save(mouvementStock);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/mouvements-stock/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!mouvementStockService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        mouvementStockService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
