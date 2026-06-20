package com.voly_saina.controller;

import com.voly_saina.entity.Pages;
import com.voly_saina.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PageController {

    @Autowired
    private PageService PageService;

    // GET /api/categories-produit/{id}
    @GetMapping
    public ResponseEntity<Pages> getById(@PathVariable Long id) {
        return PageService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/categories-produit
    @PostMapping
    public ResponseEntity<Pages> create(@RequestBody Pages Page) {
        Pages saved = PageService.save(Page);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/categories-produit/{id}
    // @PutMapping("/{id}")
    // public ResponseEntity<Page> update(@PathVariable Long id, @RequestBody Page Page) {
    //     if (!PageService.existsById(id)) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    //     }
    //     Page.setIdCategorie(id);
    //     Page updated = PageService.save(Page);
    //     return ResponseEntity.ok(updated);
    // }

}
