package com.voly_saina.service;

import com.voly_saina.entity.EtatMachine;
import com.voly_saina.repository.EtatMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtatMachineService {

    @Autowired
    private EtatMachineRepository etatMachineRepository;

    public List<EtatMachine> findAll() {
        return etatMachineRepository.findAll();
    }

    public EtatMachine findById(Long id) {
        return etatMachineRepository.findById(id);
    }

    public EtatMachine save(EtatMachine etatMachine) {
        return etatMachineRepository.save(etatMachine);
    }

    public boolean existsById(Long id) {
        return etatMachineRepository.existsById(id);
    }

    public void deleteById(Long id) {
        etatMachineRepository.deleteById(id);
    }
}
