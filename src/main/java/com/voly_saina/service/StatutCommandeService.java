package com.voly_saina.service;

import com.voly_saina.entity.StatutCommande;
import com.voly_saina.repository.StatutCommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatutCommandeService {

    @Autowired
    private StatutCommandeRepository statutCommandeRepository;

    public List<StatutCommande> findAll() {
        return statutCommandeRepository.findAll();
    }

    public Optional<StatutCommande> findById(Long id) {
        return statutCommandeRepository.findById(id);
    }

    public StatutCommande save(StatutCommande statutCommande) {
        return statutCommandeRepository.save(statutCommande);
    }

    public boolean existsById(Long id) {
        return statutCommandeRepository.existsById(id);
    }

    public void deleteById(Long id) {
        statutCommandeRepository.deleteById(id);
    }
}
