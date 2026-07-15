package com.voly_saina.service;

import com.voly_saina.entity.ModePaiement;
import com.voly_saina.repository.ModePaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModePaiementService {

    @Autowired
    private ModePaiementRepository modePaiementRepository;

    public List<ModePaiement> findAll() {
        return modePaiementRepository.findAll();
    }

    public ModePaiement findById(Long id) {
        return modePaiementRepository.findById(id).orElse(null);
    }

    public ModePaiement save(ModePaiement modePaiement) {
        return modePaiementRepository.save(modePaiement);
    }

    public boolean existsById(Long id) {
        return modePaiementRepository.existsById(id);
    }

    public void deleteById(Long id) {
        modePaiementRepository.deleteById(id);
    }
}
