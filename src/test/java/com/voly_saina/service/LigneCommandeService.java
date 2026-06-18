package com.voly_saina.service;

import com.voly_saina.entity.LigneCommande;
import com.voly_saina.repository.LigneCommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LigneCommandeService {

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    public List<LigneCommande> findAll() {
        return ligneCommandeRepository.findAll();
    }

    public Optional<LigneCommande> findById(Long id) {
        return ligneCommandeRepository.findById(id);
    }

    public LigneCommande save(LigneCommande ligneCommande) {
        return ligneCommandeRepository.save(ligneCommande);
    }

    public boolean existsById(Long id) {
        return ligneCommandeRepository.existsById(id);
    }

    public void deleteById(Long id) {
        ligneCommandeRepository.deleteById(id);
    }
}
