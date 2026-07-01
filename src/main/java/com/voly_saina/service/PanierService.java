package com.voly_saina.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.Panier;
import com.voly_saina.repository.PanierRepository;

@Service
public class PanierService {
    @Autowired
    private PanierRepository panierRepository;



    public List<Panier> findAll() {
        return panierRepository.findAll();
    }

    public Panier findById(Long id) {
        return panierRepository.findById(id).orElse(null);
    }

    public Panier save(Panier Panier) {
        return panierRepository.save(Panier);
    }

    public boolean existsById(Long id) {
        return panierRepository.existsById(id);
    }

    public void deleteById(Long id) {
        panierRepository.deleteById(id);
    }

}
