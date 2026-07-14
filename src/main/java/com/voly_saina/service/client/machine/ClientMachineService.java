package com.voly_saina.service.client.machine;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voly_saina.dto.dtoMacine.MachineCatalogueDTO;
import com.voly_saina.entity.Machine;
import com.voly_saina.service.MachineService;

@Service
public class ClientMachineService {

    @Autowired
    private MachineService machineService;

    public List<MachineCatalogueDTO> getCatalogue() {
        List<Machine> machines = machineService.findAll();
        return machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
    }

    public List<MachineCatalogueDTO> getMachinesByType(Long typeId) {
        List<Machine> machines = machineService.findByTypeMachine(typeId);
        return machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
    }

    public List<MachineCatalogueDTO> getMachinesDisponibles() {
        List<Machine> machines = machineService.findAvailableMachines();
        return machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
    }

    private MachineCatalogueDTO mapToCatalogueDTO(Machine machine) {
        MachineCatalogueDTO dto = new MachineCatalogueDTO();
        dto.setIdMachine(machine.getIdMachine());
        dto.setNom(machine.getNom());
        dto.setDescription(machine.getDescription());
        dto.setPrixJour(machine.getPrixJour());
        dto.setLocalisation(machine.getLocalisation());
        dto.setTypeMachine(machine.getTypeMachine() != null ? machine.getTypeMachine().getLibelle() : "Non défini");
        dto.setEtatMachine(machine.getEtatMachine() != null ? machine.getEtatMachine().getLibelle() : "Non défini");
        dto.setDisponible(machine.getDisponible());
        return dto;
    }
}