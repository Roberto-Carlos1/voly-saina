package com.voly_saina.service;

import com.voly_saina.entity.Facture;
import com.voly_saina.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    public List<Facture> findAll() {
        return factureRepository.findAll();
    }

    public Optional<Facture> findById(Long id) {
        return factureRepository.findById(id);
    }

    public Facture save(Facture facture) {
        return factureRepository.save(facture);
    }

    public boolean existsById(Long id) {
        return factureRepository.existsById(id);
    }

    public void deleteById(Long id) {
        factureRepository.deleteById(id);
    }
}
