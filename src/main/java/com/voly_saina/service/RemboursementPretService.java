package com.voly_saina.service;

import com.voly_saina.entity.RemboursementPret;
import com.voly_saina.repository.RemboursementPretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RemboursementPretService {

    @Autowired
    private RemboursementPretRepository remboursementPretRepository;

    public List<RemboursementPret> findAll() {
        return remboursementPretRepository.findAll();
    }

    public Optional<RemboursementPret> findById(Long id) {
        return remboursementPretRepository.findById(id);
    }

    public RemboursementPret save(RemboursementPret remboursementPret) {
        return remboursementPretRepository.save(remboursementPret);
    }

    public boolean existsById(Long id) {
        return remboursementPretRepository.existsById(id);
    }

    public void deleteById(Long id) {
        remboursementPretRepository.deleteById(id);
    }
}
