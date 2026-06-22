package com.voly_saina.service;

import com.voly_saina.entity.MouvementStock;
import com.voly_saina.repository.MouvementStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MouvementStockService {

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    public List<MouvementStock> findAll() {
        return mouvementStockRepository.findAll();
    }

    public Optional<MouvementStock> findById(Long id) {
        return mouvementStockRepository.findById(id);
    }

    public MouvementStock save(MouvementStock mouvementStock) {
        return mouvementStockRepository.save(mouvementStock);
    }

    public boolean existsById(Long id) {
        return mouvementStockRepository.existsById(id);
    }

    public void deleteById(Long id) {
        mouvementStockRepository.deleteById(id);
    }
}
