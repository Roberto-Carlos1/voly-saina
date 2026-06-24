package com.voly_saina.service;

import com.voly_saina.entity.Operation;
import com.voly_saina.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperationService {

    @Autowired
    private OperationRepository operationRepository;

    public List<Operation> findAll() {
        return operationRepository.findAll();
    }

    public Optional<Operation> findById(Long id) {
        return operationRepository.findById(id);
    }

    public Operation save(Operation Operation) {
        return operationRepository.save(Operation);
    }

    public boolean existsById(Long id) {
        return operationRepository.existsById(id);
    }

    public void deleteById(Long id) {
        operationRepository.deleteById(id);
    }
}
