package com.voly_saina.controller.paiement;

import com.voly_saina.entity.Operation;
import com.voly_saina.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operation")
public class OperationController {

    @Autowired
    private OperationService operationService;

    // GET /api/operation
    @GetMapping
    public ResponseEntity<List<Operation>> getAll() {
        return ResponseEntity.ok(operationService.findAll());
    }

    // GET /api/operation/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Operation> getById(@PathVariable Long id) {
        return operationService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/operation
    @PostMapping
    public ResponseEntity<Operation> create(@RequestBody Operation Operation) {
        Operation saved = operationService.save(Operation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/operation/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Operation> update(@PathVariable Long id, @RequestBody Operation operation) {
        if (!operationService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        operation.setIdOperation(id);
        Operation updated = operationService.save(operation);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/operation/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!operationService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        operationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
