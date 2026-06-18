package com.voly_saina.service;

import com.voly_saina.entity.PretBancaire;
import com.voly_saina.repository.PretBancaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PretBancaireService {

    @Autowired
    private PretBancaireRepository pretBancaireRepository;

    public List<PretBancaire> findAll() {
        return pretBancaireRepository.findAll();
    }

    public Optional<PretBancaire> findById(Long id) {
        return pretBancaireRepository.findById(id);
    }

    public PretBancaire save(PretBancaire pretBancaire) {
        return pretBancaireRepository.save(pretBancaire);
    }

    public boolean existsById(Long id) {
        return pretBancaireRepository.existsById(id);
    }

    public void deleteById(Long id) {
        pretBancaireRepository.deleteById(id);
    }
}
