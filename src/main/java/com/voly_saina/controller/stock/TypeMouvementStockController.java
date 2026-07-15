package com.voly_saina.controller.stock;

import com.voly_saina.entity.TypeMouvementStock;
import com.voly_saina.service.TypeMouvementStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-mouvement-stock")
public class TypeMouvementStockController {

    @Autowired
    private TypeMouvementStockService typeMouvementStockService;

    // GET /api/types-mouvement-stock
    @GetMapping
    public ResponseEntity<List<TypeMouvementStock>> getAll() {
        return ResponseEntity.ok(typeMouvementStockService.findAll());
    }

    // GET /api/types-mouvement-stock/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TypeMouvementStock> getById(@PathVariable Long id) {
        return typeMouvementStockService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/types-mouvement-stock
    @PostMapping
    public ResponseEntity<TypeMouvementStock> create(@RequestBody TypeMouvementStock typeMouvementStock) {
        TypeMouvementStock saved = typeMouvementStockService.save(typeMouvementStock);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/types-mouvement-stock/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TypeMouvementStock> update(@PathVariable Long id, @RequestBody TypeMouvementStock typeMouvementStock) {
        if (!typeMouvementStockService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        typeMouvementStock.setIdTypeMouvement(id);
        TypeMouvementStock updated = typeMouvementStockService.save(typeMouvementStock);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/types-mouvement-stock/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!typeMouvementStockService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        typeMouvementStockService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
