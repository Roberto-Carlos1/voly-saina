package com.voly_saina.service;

import com.voly_saina.entity.TacheEmploye;
import com.voly_saina.repository.TacheEmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TacheEmployeService {

    @Autowired
    private TacheEmployeRepository tacheEmployeRepository;

    public List<TacheEmploye> findAll() {
        return tacheEmployeRepository.findAll();
    }

    public Optional<TacheEmploye> findById(Long id) {
        return tacheEmployeRepository.findById(id);
    }

    public TacheEmploye save(TacheEmploye tacheEmploye) {
        return tacheEmployeRepository.save(tacheEmploye);
    }

    public boolean existsById(Long id) {
        return tacheEmployeRepository.existsById(id);
    }

    public void deleteById(Long id) {
        tacheEmployeRepository.deleteById(id);
    }
}
