package com.voly_saina.controller.client;

import com.voly_saina.entity.NoteClient;
import com.voly_saina.service.NoteClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes-client")
public class NoteClientController {

    @Autowired
    private NoteClientService noteClientService;

    // GET /api/notes-client
    @GetMapping
    public ResponseEntity<List<NoteClient>> getAll() {
        return ResponseEntity.ok(noteClientService.findAll());
    }

    // GET /api/notes-client/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NoteClient> getById(@PathVariable Long id) {
        return noteClientService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/notes-client
    @PostMapping
    public ResponseEntity<NoteClient> create(@RequestBody NoteClient noteClient) {
        NoteClient saved = noteClientService.save(noteClient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/notes-client/{id}
    @PutMapping("/{id}")
    public ResponseEntity<NoteClient> update(@PathVariable Long id, @RequestBody NoteClient noteClient) {
        if (!noteClientService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        noteClient.setIdNote(id);
        NoteClient updated = noteClientService.save(noteClient);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/notes-client/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!noteClientService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        noteClientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
