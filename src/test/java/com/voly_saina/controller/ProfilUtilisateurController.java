package com.voly_saina.controller;

import com.voly_saina.entity.ProfilUtilisateur;
import com.voly_saina.service.ProfilUtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profils-utilisateur")
public class ProfilUtilisateurController {

    @Autowired
    private ProfilUtilisateurService profilUtilisateurService;

    // GET /api/profils-utilisateur
    @GetMapping
    public ResponseEntity<List<ProfilUtilisateur>> getAll() {
        return ResponseEntity.ok(profilUtilisateurService.findAll());
    }

    // GET /api/profils-utilisateur/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProfilUtilisateur> getById(@PathVariable Long id) {
        return profilUtilisateurService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/profils-utilisateur
    @PostMapping
    public ResponseEntity<ProfilUtilisateur> create(@RequestBody ProfilUtilisateur profilUtilisateur) {
        ProfilUtilisateur saved = profilUtilisateurService.save(profilUtilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/profils-utilisateur/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProfilUtilisateur> update(@PathVariable Long id, @RequestBody ProfilUtilisateur profilUtilisateur) {
        if (!profilUtilisateurService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        profilUtilisateur.setIdProfil(id);
        ProfilUtilisateur updated = profilUtilisateurService.save(profilUtilisateur);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/profils-utilisateur/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!profilUtilisateurService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        profilUtilisateurService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
