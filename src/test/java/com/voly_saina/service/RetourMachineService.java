package com.voly_saina.service;

import com.voly_saina.entity.RetourMachine;
import com.voly_saina.repository.RetourMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RetourMachineService {

    @Autowired
    private RetourMachineRepository retourMachineRepository;

    public List<RetourMachine> findAll() {
        return retourMachineRepository.findAll();
    }

    public Optional<RetourMachine> findById(Long id) {
        return retourMachineRepository.findById(id);
    }

    public RetourMachine save(RetourMachine retourMachine) {
        return retourMachineRepository.save(retourMachine);
    }

    public boolean existsById(Long id) {
        return retourMachineRepository.existsById(id);
    }

    public void deleteById(Long id) {
        retourMachineRepository.deleteById(id);
    }
}
