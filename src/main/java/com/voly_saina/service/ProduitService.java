package com.voly_saina.service;

import com.voly_saina.entity.Produit;
import com.voly_saina.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    public Optional<Produit> findById(Long id) {
        return produitRepository.findById(id);
    }

    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }

    public boolean existsById(Long id) {
        return produitRepository.existsById(id);
    }

    public void deleteById(Long id) {
        produitRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Produit> findActifs() {
        return produitRepository.findByActifTrue();
    }
}
