package com.voly_saina.service;

import com.voly_saina.entity.Rapport;
import com.voly_saina.repository.RapportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RapportService {

    @Autowired
    private RapportRepository rapportRepository;

    public List<Rapport> findAll() {
        return rapportRepository.findAll();
    }

    public Optional<Rapport> findById(Long id) {
        return rapportRepository.findById(id);
    }

    public Rapport save(Rapport rapport) {
        return rapportRepository.save(rapport);
    }

    public boolean existsById(Long id) {
        return rapportRepository.existsById(id);
    }

    public void deleteById(Long id) {
        rapportRepository.deleteById(id);
    }
}
