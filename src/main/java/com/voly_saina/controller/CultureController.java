package com.voly_saina.controller;

import com.voly_saina.entity.Culture;
import com.voly_saina.service.CultureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cultures")
public class CultureController {

    @Autowired
    private CultureService cultureService;

    // GET /api/cultures
    @GetMapping
    public ResponseEntity<List<Culture>> getAll() {
        return ResponseEntity.ok(cultureService.findAll());
    }

    // GET /api/cultures/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Culture> getById(@PathVariable Long id) {
        return cultureService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/cultures
    @PostMapping
    public ResponseEntity<Culture> create(@RequestBody Culture culture) {
        Culture saved = cultureService.save(culture);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/cultures/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Culture> update(@PathVariable Long id, @RequestBody Culture culture) {
        if (!cultureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        culture.setIdCulture(id);
        Culture updated = cultureService.save(culture);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/cultures/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!cultureService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        cultureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
