package com.voly_saina.service;

import com.voly_saina.entity.FicheCulture;
import com.voly_saina.repository.FicheCultureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FicheCultureService {

    @Autowired
    private FicheCultureRepository ficheCultureRepository;

    public List<FicheCulture> findAll() {
        return ficheCultureRepository.findAll();
    }

    public Optional<FicheCulture> findById(Long id) {
        return ficheCultureRepository.findById(id);
    }

    public FicheCulture save(FicheCulture ficheCulture) {
        return ficheCultureRepository.save(ficheCulture);
    }

    public boolean existsById(Long id) {
        return ficheCultureRepository.existsById(id);
    }

    public void deleteById(Long id) {
        ficheCultureRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<FicheCulture> findByCultureId(Long idCulture) {
        return ficheCultureRepository.findByCultureIdCulture(idCulture);
    }
}
