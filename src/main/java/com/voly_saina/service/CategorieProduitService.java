package com.voly_saina.service;

import com.voly_saina.entity.CategorieProduit;
import com.voly_saina.repository.CategorieProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieProduitService {

    @Autowired
    private CategorieProduitRepository categorieProduitRepository;

    public List<CategorieProduit> findAll() {
        return categorieProduitRepository.findAll();
    }

    public Optional<CategorieProduit> findById(Long id) {
        return categorieProduitRepository.findById(id);
    }

    public CategorieProduit save(CategorieProduit categorieProduit) {
        return categorieProduitRepository.save(categorieProduit);
    }

    public boolean existsById(Long id) {
        return categorieProduitRepository.existsById(id);
    }

    public void deleteById(Long id) {
        categorieProduitRepository.deleteById(id);
    }
}
