package com.voly_saina.controller;

import com.voly_saina.entity.RetourMachine;
import com.voly_saina.service.RetourMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retours-machine")
public class RetourMachineController {

    @Autowired
    private RetourMachineService retourMachineService;

    @GetMapping
    public ResponseEntity<List<RetourMachine>> getAll() {
        return ResponseEntity.ok(retourMachineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetourMachine> getById(@PathVariable Long id) {
        return retourMachineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<RetourMachine> create(@RequestBody RetourMachine retourMachine) {
        RetourMachine saved = retourMachineService.enregistrerRetour(retourMachine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RetourMachine> update(@PathVariable Long id, @RequestBody RetourMachine retourMachine) {
        if (!retourMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        retourMachine.setIdRetour(id);
        RetourMachine updated = retourMachineService.save(retourMachine);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!retourMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        retourMachineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}