package com.voly_saina.service;

import com.voly_saina.entity.StatutFacture;
import com.voly_saina.repository.StatutFactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatutFactureService {

    @Autowired
    private StatutFactureRepository statutFactureRepository;

    public List<StatutFacture> findAll() {
        return statutFactureRepository.findAll();
    }

    public Optional<StatutFacture> findById(Long id) {
        return statutFactureRepository.findById(id);
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

    public Optional<StatutFacture> findByCode(String code) {
        return statutFactureRepository.findByCode(code);
    }
}
