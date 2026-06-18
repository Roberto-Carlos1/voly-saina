package com.voly_saina.controller;

import com.voly_saina.entity.TacheEmploye;
import com.voly_saina.service.TacheEmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taches-employe")
public class TacheEmployeController {

    @Autowired
    private TacheEmployeService tacheEmployeService;

    // GET /api/taches-employe
    @GetMapping
    public ResponseEntity<List<TacheEmploye>> getAll() {
        return ResponseEntity.ok(tacheEmployeService.findAll());
    }

    // GET /api/taches-employe/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TacheEmploye> getById(@PathVariable Long id) {
        return tacheEmployeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/taches-employe
    @PostMapping
    public ResponseEntity<TacheEmploye> create(@RequestBody TacheEmploye tacheEmploye) {
        TacheEmploye saved = tacheEmployeService.save(tacheEmploye);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/taches-employe/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TacheEmploye> update(@PathVariable Long id, @RequestBody TacheEmploye tacheEmploye) {
        if (!tacheEmployeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        tacheEmploye.setIdTache(id);
        TacheEmploye updated = tacheEmployeService.save(tacheEmploye);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/taches-employe/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!tacheEmployeService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        tacheEmployeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
