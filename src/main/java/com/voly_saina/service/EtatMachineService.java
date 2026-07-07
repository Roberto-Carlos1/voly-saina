package com.voly_saina.service;

import com.voly_saina.entity.EtatMachine;
import com.voly_saina.repository.EtatMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EtatMachineService {

    @Autowired
    private EtatMachineRepository etatMachineRepository;

    @org.springframework.cache.annotation.Cacheable("etatMachines")
    public List<EtatMachine> findAll() {
        return etatMachineRepository.findAll();
    }

    @org.springframework.cache.annotation.Cacheable(value = "etatMachines", key = "#code")
    public Optional<EtatMachine> findByCode(String code) {
        return etatMachineRepository.findByCode(code);
    }
    
    @org.springframework.cache.annotation.Cacheable(value = "etatMachines", key = "#id")
    public EtatMachine findById(Long id) {
        return etatMachineRepository.findById(id).orElse(null);
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
