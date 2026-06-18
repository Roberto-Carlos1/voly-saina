package com.voly_saina.service;

import com.voly_saina.entity.NoteClient;
import com.voly_saina.repository.NoteClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteClientService {

    @Autowired
    private NoteClientRepository noteClientRepository;

    public List<NoteClient> findAll() {
        return noteClientRepository.findAll();
    }

    public Optional<NoteClient> findById(Long id) {
        return noteClientRepository.findById(id);
    }

    public NoteClient save(NoteClient noteClient) {
        return noteClientRepository.save(noteClient);
    }

    public boolean existsById(Long id) {
        return noteClientRepository.existsById(id);
    }

    public void deleteById(Long id) {
        noteClientRepository.deleteById(id);
    }
}
