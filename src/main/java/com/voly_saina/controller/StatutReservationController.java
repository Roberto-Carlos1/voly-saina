package com.voly_saina.controller;

import com.voly_saina.entity.StatutReservation;
import com.voly_saina.service.StatutReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuts-reservation")
public class StatutReservationController {

    @Autowired
    private StatutReservationService statutReservationService;

    // GET /api/statuts-reservation
    @GetMapping
    public ResponseEntity<List<StatutReservation>> getAll() {
        return ResponseEntity.ok(statutReservationService.findAll());
    }

    // GET /api/statuts-reservation/{id}
    @GetMapping("/{id}")
    public ResponseEntity<StatutReservation> getById(@PathVariable Long id) {
        return statutReservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/statuts-reservation
    @PostMapping
    public ResponseEntity<StatutReservation> create(@RequestBody StatutReservation statutReservation) {
        StatutReservation saved = statutReservationService.save(statutReservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/statuts-reservation/{id}
    @PutMapping("/{id}")
    public ResponseEntity<StatutReservation> update(@PathVariable Long id, @RequestBody StatutReservation statutReservation) {
        if (!statutReservationService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutReservation.setIdStatutReservation(id);
        StatutReservation updated = statutReservationService.save(statutReservation);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/statuts-reservation/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!statutReservationService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        statutReservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
