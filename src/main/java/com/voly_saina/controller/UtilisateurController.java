package com.voly_saina.controller;

import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    // GET /api/utilisateurs
    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAll() {
        return ResponseEntity.ok(utilisateurService.findAll());
    }

    // GET /api/utilisateurs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getById(@PathVariable Long id) {
        return utilisateurService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/utilisateurs
    @PostMapping
    public ResponseEntity<Utilisateur> create(@RequestBody Utilisateur utilisateur) {
        Utilisateur saved = utilisateurService.save(utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/utilisateurs/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> update(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        if (!utilisateurService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        utilisateur.setIdUtilisateur(id);
        Utilisateur updated = utilisateurService.save(utilisateur);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/utilisateurs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!utilisateurService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        utilisateurService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
