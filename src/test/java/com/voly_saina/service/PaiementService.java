package com.voly_saina.service;

import com.voly_saina.entity.Paiement;
import com.voly_saina.repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    public Optional<Paiement> findById(Long id) {
        return paiementRepository.findById(id);
    }

    public Paiement save(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public boolean existsById(Long id) {
        return paiementRepository.existsById(id);
    }

    public void deleteById(Long id) {
        paiementRepository.deleteById(id);
    }
}
