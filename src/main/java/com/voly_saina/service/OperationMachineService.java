package com.voly_saina.service;

import com.voly_saina.entity.OperationMachine;
import com.voly_saina.repository.OperationMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperationMachineService {

    @Autowired
    private OperationMachineRepository operationMachineRepository;

    public List<OperationMachine> findAll() {
        return operationMachineRepository.findAll();
    }

    public Optional<OperationMachine> findById(Long id) {
        return operationMachineRepository.findById(id);
    }

    public void save(OperationMachine OperationMachine) {
        operationMachineRepository.save(OperationMachine);
    }

    public boolean existsById(Long id) {
        return operationMachineRepository.existsById(id);
    }

    public void deleteById(Long id) {
        operationMachineRepository.deleteById(id);
    }
}
