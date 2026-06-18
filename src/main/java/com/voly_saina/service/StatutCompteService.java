package com.voly_saina.service;

import com.voly_saina.entity.StatutCompte;
import com.voly_saina.repository.StatutCompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatutCompteService {

    @Autowired
    private StatutCompteRepository statutCompteRepository;

    public List<StatutCompte> findAll() {
        return statutCompteRepository.findAll();
    }

    public Optional<StatutCompte> findById(Long id) {
        return statutCompteRepository.findById(id);
    }

    public StatutCompte save(StatutCompte statutCompte) {
        return statutCompteRepository.save(statutCompte);
    }

    public boolean existsById(Long id) {
        return statutCompteRepository.existsById(id);
    }

    public void deleteById(Long id) {
        statutCompteRepository.deleteById(id);
    }
}
