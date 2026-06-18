package com.voly_saina.controller;

import com.voly_saina.entity.Machine;
import com.voly_saina.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
public class MachineController {

    @Autowired
    private MachineService machineService;

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    // GET /api/machines
    @GetMapping
    public ResponseEntity<List<Machine>> getAll() {
        return ResponseEntity.ok(machineService.findAll());
    }
    
    
    // GET /api/machines/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Machine> getById(@PathVariable Long id) {
        return machineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/machines
    @PostMapping
    public ResponseEntity<Machine> create(@RequestBody Machine machine) {
        Machine saved = machineService.save(machine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/machines/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Machine> update(@PathVariable Long id, @RequestBody Machine machine) {
        if (!machineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        machine.setIdMachine(id);
        Machine updated = machineService.save(machine);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/machines/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!machineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        machineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
