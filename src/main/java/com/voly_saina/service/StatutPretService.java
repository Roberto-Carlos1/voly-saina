package com.voly_saina.service;

import com.voly_saina.entity.StatutPret;
import com.voly_saina.repository.StatutPretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatutPretService {

    @Autowired
    private StatutPretRepository statutPretRepository;

    public List<StatutPret> findAll() {
        return statutPretRepository.findAll();
    }

    public Optional<StatutPret> findById(Long id) {
        return statutPretRepository.findById(id);
    }

    public StatutPret save(StatutPret statutPret) {
        return statutPretRepository.save(statutPret);
    }

    public boolean existsById(Long id) {
        return statutPretRepository.existsById(id);
    }

    public void deleteById(Long id) {
        statutPretRepository.deleteById(id);
    }
}
