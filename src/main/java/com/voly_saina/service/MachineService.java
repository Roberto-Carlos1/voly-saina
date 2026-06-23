package com.voly_saina.service;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.TypeMachine;
import com.voly_saina.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MachineService {

    @Autowired
    private MachineRepository machineRepository;

    public List<Machine> findAll() {
        return machineRepository.findAll();
    }

    public Machine findById(Long id) {
        return machineRepository.findByIdWithRelations(id).orElse(null);
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

    public Page<Machine> findByPage(Pageable pageable) {
        Page<Machine> page = machineRepository.findAll(pageable);
        List<Long> ids = new ArrayList<>();

        for (Machine machine : page.getContent()) {
            ids.add(machine.getIdMachine());
        }

        if (ids.isEmpty()) {
            return page;
        }

        List<Machine> machinesWithRelations = machineRepository.findByIdMachineInWithRelations(ids);
        List<Machine> machines = new ArrayList<>();

        for (Long id : ids) {
            for (Machine machine : machinesWithRelations) {
                if (machine.getIdMachine().equals(id)) {
                    machines.add(machine);
                    break;
                }
            }
        }

        return new PageImpl<>(machines, pageable, page.getTotalElements());
    }

    public Page<Machine> filtrerMachine(String idtype, String nom, Pageable page) {
        Machine machineExemple = new Machine();
        machineExemple.setDisponible(null);

        if (nom != null && !nom.trim().isEmpty()) {
            machineExemple.setNom(nom);
        }

        if (idtype != null && !idtype.trim().isEmpty()) {
            TypeMachine type = new TypeMachine();
            type.setIdTypeMachine(Long.parseLong(idtype));
            machineExemple.setTypeMachine(type);
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Équivalent de LIKE %texte%
                .withIgnoreCase();

        Example<Machine> exemple = Example.of(machineExemple, matcher);
        return machineRepository.findAll(exemple, page);
    }
}
