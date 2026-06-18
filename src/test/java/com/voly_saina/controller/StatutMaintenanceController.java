package com.voly_saina.controller;

import com.voly_saina.entity.StatutMaintenance;
import com.voly_saina.service.StatutMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts-maintenance")
public class StatutMaintenanceController {

    @Autowired
    private StatutMaintenanceService statutMaintenanceService;

    // GET /api/statuts-maintenance
    @GetMapping
    public ResponseEntity<List<StatutMaintenance>> getAll() {
        return ResponseEntity.ok(statutMaintenanceService.findAll());
    }

    // GET /api/statuts-maintenance/{id}
    @GetMapping("/{id}")
    public ResponseEntity<StatutMaintenance> getById(@PathVariable Long id) {
        return statutMaintenanceService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/statuts-maintenance
    @PostMapping
    public ResponseEntity<StatutMaintenance> create(@RequestBody StatutMaintenance statutMaintenance) {
        StatutMaintenance saved = statutMaintenanceService.save(statutMaintenance);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/statuts-maintenance/{id}
    @PutMapping("/{id}")
    public ResponseEntity<StatutMaintenance> update(@PathVariable Long id, @RequestBody StatutMaintenance statutMaintenance) {
        if (!statutMaintenanceService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutMaintenance.setIdStatutMaintenance(id);
        StatutMaintenance updated = statutMaintenanceService.save(statutMaintenance);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/statuts-maintenance/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!statutMaintenanceService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutMaintenanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
