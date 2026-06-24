package com.voly_saina.controller.machine;

import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.service.MaintenanceMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances-machine")
public class MaintenanceMachineController {

    @Autowired
    private MaintenanceMachineService maintenanceMachineService;

    // GET /api/maintenances-machine
    @GetMapping
    public ResponseEntity<List<MaintenanceMachine>> getAll() {
        return ResponseEntity.ok(maintenanceMachineService.findAll());
    }

    // GET /api/maintenances-machine/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceMachine> getById(@PathVariable Long id) {
        return maintenanceMachineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/maintenances-machine
    @PostMapping
    public ResponseEntity<MaintenanceMachine> create(@RequestBody MaintenanceMachine maintenanceMachine) {
        MaintenanceMachine saved = maintenanceMachineService.save(maintenanceMachine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/maintenances-machine/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceMachine> update(@PathVariable Long id, @RequestBody MaintenanceMachine maintenanceMachine) {
        if (!maintenanceMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        maintenanceMachine.setIdMaintenance(id);
        MaintenanceMachine updated = maintenanceMachineService.save(maintenanceMachine);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/maintenances-machine/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!maintenanceMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        maintenanceMachineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
