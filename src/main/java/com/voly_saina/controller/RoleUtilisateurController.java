package com.voly_saina.controller;

import com.voly_saina.entity.RoleUtilisateur;
import com.voly_saina.service.RoleUtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles-utilisateur")
public class RoleUtilisateurController {

    @Autowired
    private RoleUtilisateurService roleUtilisateurService;

    // GET /api/roles-utilisateur
    @GetMapping
    public ResponseEntity<List<RoleUtilisateur>> getAll() {
        return ResponseEntity.ok(roleUtilisateurService.findAll());
    }

    // GET /api/roles-utilisateur/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RoleUtilisateur> getById(@PathVariable Long id) {
        return roleUtilisateurService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/roles-utilisateur
    @PostMapping
    public ResponseEntity<RoleUtilisateur> create(@RequestBody RoleUtilisateur roleUtilisateur) {
        RoleUtilisateur saved = roleUtilisateurService.save(roleUtilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/roles-utilisateur/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RoleUtilisateur> update(@PathVariable Long id, @RequestBody RoleUtilisateur roleUtilisateur) {
        if (!roleUtilisateurService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        roleUtilisateur.setIdRole(id);
        RoleUtilisateur updated = roleUtilisateurService.save(roleUtilisateur);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/roles-utilisateur/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!roleUtilisateurService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        roleUtilisateurService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
