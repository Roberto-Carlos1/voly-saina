package com.voly_saina.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.TypeMachine;
import com.voly_saina.repository.TypeMachineRepository;

@Service
public class TypeMachineService {
    @Autowired
    private TypeMachineRepository typeMachineRepository;

    public List<TypeMachine> findAll() {
        return typeMachineRepository.findAll();
    }

    public Optional<TypeMachine> findById(Long id) {
        return typeMachineRepository.findById(id);
    }
}   
