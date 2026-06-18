package com.voly_saina.service;

import com.voly_saina.entity.Culture;
import com.voly_saina.repository.CultureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CultureService {

    @Autowired
    private CultureRepository cultureRepository;

    public List<Culture> findAll() {
        return cultureRepository.findAll();
    }

    public Optional<Culture> findById(Long id) {
        return cultureRepository.findById(id);
    }

    public Culture save(Culture culture) {
        return cultureRepository.save(culture);
    }

    public boolean existsById(Long id) {
        return cultureRepository.existsById(id);
    }

    public void deleteById(Long id) {
        cultureRepository.deleteById(id);
    }
}
