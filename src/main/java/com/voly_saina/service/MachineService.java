package com.voly_saina.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.Machine;
import com.voly_saina.repository.MachineRepository;

@Service
public class MachineService {

    @Autowired
    private MachineRepository machineRepository;

    public List<Machine> findAll() {
        return machineRepository.findAll();
    }

    public Machine findById(Long id) {
        return machineRepository.findById(id).orElse(null);
    }

    public Machine save(Machine machine) {
        return machineRepository.save(machine);
    }

    public boolean existsById(Long id) {
        return machineRepository.existsById(id);
    }

    public void deleteById(Long id) {
        machineRepository.deleteById(id);
    }

    public List<Machine> findByTypeMachine(Long typeId) {
        return machineRepository.findByTypeMachineId(typeId);
    }

    public List<Machine> findAvailableMachines() {
        return machineRepository.findAvailableMachines();
    }

    
}
