package com.voly_saina.service;

import com.voly_saina.entity.StatutFacture;
import com.voly_saina.repository.StatutFactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatutFactureService {

    @Autowired
    private StatutFactureRepository statutFactureRepository;

    public List<StatutFacture> findAll() {
        return statutFactureRepository.findAll();
    }

    public StatutFacture findById(Long id) {
        return statutFactureRepository.findById(id).orElse(null);
    }

    public StatutFacture save(StatutFacture statutFacture) {
        return statutFactureRepository.save(statutFacture);
    }

    public boolean existsById(Long id) {
        return statutFactureRepository.existsById(id);
    }

    public void deleteById(Long id) {
        statutFactureRepository.deleteById(id);
    }
}
