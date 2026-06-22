package com.voly_saina.service;

import com.voly_saina.entity.StatutTache;
import com.voly_saina.repository.StatutTacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatutTacheService {

    @Autowired
    private StatutTacheRepository statutTacheRepository;

    public List<StatutTache> findAll() {
        return statutTacheRepository.findAll();
    }

    public Optional<StatutTache> findById(Long id) {
        return statutTacheRepository.findById(id);
    }

    public StatutTache save(StatutTache statutTache) {
        return statutTacheRepository.save(statutTache);
    }

    public boolean existsById(Long id) {
        return statutTacheRepository.existsById(id);
    }

    public void deleteById(Long id) {
        statutTacheRepository.deleteById(id);
    }
}
