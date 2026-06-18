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

    // GET /api/retours-machine
    @GetMapping
    public ResponseEntity<List<RetourMachine>> getAll() {
        return ResponseEntity.ok(retourMachineService.findAll());
    }

    // GET /api/retours-machine/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RetourMachine> getById(@PathVariable Long id) {
        return retourMachineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/retours-machine
    @PostMapping
    public ResponseEntity<RetourMachine> create(@RequestBody RetourMachine retourMachine) {
        RetourMachine saved = retourMachineService.save(retourMachine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/retours-machine/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RetourMachine> update(@PathVariable Long id, @RequestBody RetourMachine retourMachine) {
        if (!retourMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        retourMachine.setIdRetour(id);
        RetourMachine updated = retourMachineService.save(retourMachine);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/retours-machine/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!retourMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        retourMachineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
