package com.voly_saina.controller;

import com.voly_saina.entity.EtatMachine;
import com.voly_saina.service.EtatMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etats-machine")
public class EtatMachineController {

    @Autowired
    private EtatMachineService etatMachineService;

    // GET /api/etats-machine
    @GetMapping
    public ResponseEntity<List<EtatMachine>> getAll() {
        return ResponseEntity.ok(etatMachineService.findAll());
    }

    // GET /api/etats-machine/{id}
    @GetMapping("/{id}")
    // public ResponseEntity<EtatMachine> getById(@PathVariable Long id) {
    //     return etatMachineService.findById(id)
    //             .map(ResponseEntity::ok)
    //             .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    // }

    // POST /api/etats-machine
    @PostMapping
    public ResponseEntity<EtatMachine> create(@RequestBody EtatMachine etatMachine) {
        EtatMachine saved = etatMachineService.save(etatMachine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/etats-machine/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EtatMachine> update(@PathVariable Long id, @RequestBody EtatMachine etatMachine) {
        if (!etatMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        etatMachine.setIdEtatMachine(id);
        EtatMachine updated = etatMachineService.save(etatMachine);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/etats-machine/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!etatMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        etatMachineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
