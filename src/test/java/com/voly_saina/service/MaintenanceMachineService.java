package com.voly_saina.service;

import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.repository.MaintenanceMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceMachineService {

    @Autowired
    private MaintenanceMachineRepository maintenanceMachineRepository;

    public List<MaintenanceMachine> findAll() {
        return maintenanceMachineRepository.findAll();
    }

    public Optional<MaintenanceMachine> findById(Long id) {
        return maintenanceMachineRepository.findById(id);
    }

    public MaintenanceMachine save(MaintenanceMachine maintenanceMachine) {
        return maintenanceMachineRepository.save(maintenanceMachine);
    }

    public boolean existsById(Long id) {
        return maintenanceMachineRepository.existsById(id);
    }

    public void deleteById(Long id) {
        maintenanceMachineRepository.deleteById(id);
    }
}
