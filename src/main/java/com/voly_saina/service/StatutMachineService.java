package com.voly_saina.service;

import com.voly_saina.entity.StatutMachine;
import com.voly_saina.repository.StatutMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StatutMachineService {

    @Autowired
    private StatutMachineRepository repository;

    public List<StatutMachine> findAll() {
        return repository.findAll();
    }

    public StatutMachine findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public StatutMachine save(StatutMachine statutMachine) {
        return repository.save(statutMachine);
    }

    public StatutMachine findCurrentByMachineId(Long idMachine) {
        return repository.findTopByMachineIdMachineOrderByDateCreationDescIdDesc(idMachine);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
