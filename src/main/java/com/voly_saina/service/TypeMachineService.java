package com.voly_saina.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.entity.TypeMachine;
import com.voly_saina.repository.TypeMachineRepository;

@Service
public class TypeMachineService {
    @Autowired
    private TypeMachineRepository typeMachineRepository;

    @org.springframework.cache.annotation.Cacheable("typeMachines")
    public List<TypeMachine> findAll() {
        return typeMachineRepository.findAll();
    }
    
    @org.springframework.cache.annotation.Cacheable(value = "typeMachines", key = "#id")
    public TypeMachine findById(Long id) {
        return typeMachineRepository.findById(id).orElse(null);
    }
}
