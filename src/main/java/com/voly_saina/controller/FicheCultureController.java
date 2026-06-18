package com.voly_saina.controller;

import com.voly_saina.entity.FicheCulture;
import com.voly_saina.service.FicheCultureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fiches-culture")
public class FicheCultureController {

    @Autowired
    private FicheCultureService ficheCultureService;

    // GET /api/fiches-culture
    @GetMapping
    public ResponseEntity<List<FicheCulture>> getAll() {
        return ResponseEntity.ok(ficheCultureService.findAll());
    }

    // GET /api/fiches-culture/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FicheCulture> getById(@PathVariable Long id) {
        return ficheCultureService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/fiches-culture
    @PostMapping
    public ResponseEntity<FicheCulture> create(@RequestBody FicheCulture ficheCulture) {
        FicheCulture saved = ficheCultureService.save(ficheCulture);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/fiches-culture/{id}
    @PutMapping("/{id}")
    public ResponseEntity<FicheCulture> update(@PathVariable Long id, @RequestBody FicheCulture ficheCulture) {
        if (!ficheCultureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ficheCulture.setIdFiche(id);
        FicheCulture updated = ficheCultureService.save(ficheCulture);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/fiches-culture/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!ficheCultureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ficheCultureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
