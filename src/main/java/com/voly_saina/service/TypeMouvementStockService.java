package com.voly_saina.service;

import com.voly_saina.entity.TypeMouvementStock;
import com.voly_saina.repository.TypeMouvementStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeMouvementStockService {

    @Autowired
    private TypeMouvementStockRepository typeMouvementStockRepository;

    public List<TypeMouvementStock> findAll() {
        return typeMouvementStockRepository.findAll();
    }

    public Optional<TypeMouvementStock> findById(Long id) {
        return typeMouvementStockRepository.findById(id);
    }

    public TypeMouvementStock save(TypeMouvementStock typeMouvementStock) {
        return typeMouvementStockRepository.save(typeMouvementStock);
    }

    public boolean existsById(Long id) {
        return typeMouvementStockRepository.existsById(id);
    }

    public void deleteById(Long id) {
        typeMouvementStockRepository.deleteById(id);
    }
}
