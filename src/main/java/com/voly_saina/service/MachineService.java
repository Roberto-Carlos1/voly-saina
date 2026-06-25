package com.voly_saina.service;

import java.util.ArrayList;
import java.util.List;

import com.voly_saina.entity.EtatMachine;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.StatutMachine;
import com.voly_saina.entity.TypeMachine;
import com.voly_saina.repository.MachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voly_saina.entity.Machine;
import com.voly_saina.repository.MachineRepository;

@Service
public class MachineService {

    @Autowired
    private MachineRepository machineRepository;

    private final StatutMachineService statutMachineService;

    public MachineService(StatutMachineService statutMachineService) {
        this.statutMachineService = statutMachineService;
    }

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

    public Page<Machine> filtrerMachine(String idtype, String idetat, String nom, Pageable pageable) {
        String nomFiltre = (nom != null && !nom.trim().isEmpty()) ? nom : null;
        Long idTypeFiltre = null;

        if (idtype != null && !idtype.trim().isEmpty()) {
            idTypeFiltre = Long.parseLong(idtype);
        }
        Long idEtatFiltre = null;
        if (idetat != null && !idetat.trim().isEmpty()) {
            idEtatFiltre = Long.parseLong(idetat);
        }
        return machineRepository.filtrerLesMachines(nomFiltre, idTypeFiltre, idEtatFiltre, pageable);
    }

    public List<Machine> findByTypeMachine(Long typeId) {
        return machineRepository.findByTypeMachineId(typeId);
    }

    public List<Machine> findAvailableMachines() {
        return machineRepository.findAvailableMachines();
    }


    @Transactional(readOnly = true)
    public List<Machine> findDisponibles() {
        return machineRepository.findByDisponibleTrue();
    }
}
