package com.voly_saina.controller.client.machine;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voly_saina.dto.dtoMacine.MachineCatalogueDTO;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.TypeMachine;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.TypeMachineService;

@RestController
@RequestMapping("/api/client/machines")
public class ClientMachineController {

    @Autowired
    private MachineService machineService;

    @Autowired
    private TypeMachineService typeMachineService;

    
    // GET /api/client/machines/catalogue
    @GetMapping("/catalogue")
    public ResponseEntity<List<MachineCatalogueDTO>> getCatalogue() {
        List<Machine> machines = machineService.findAll();
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/client/machines/type/{typeId}
    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<MachineCatalogueDTO>> getMachinesByType(@PathVariable Long typeId) {
        List<Machine> machines = machineService.findByTypeMachine(typeId);
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/client/machines/disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<MachineCatalogueDTO>> getMachinesDisponibles() {
        List<Machine> machines = machineService.findAvailableMachines();
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/client/machines/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MachineCatalogueDTO> getMachineById(@PathVariable Long id) {
        Machine machine = machineService.findById(id);
        if (machine == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToCatalogueDTO(machine));
    }

    // GET /api/client/machines/types
    @GetMapping("/types")
    public ResponseEntity<List<TypeMachine>> getTypes() {
        return ResponseEntity.ok(typeMachineService.findAll());
    }

    private MachineCatalogueDTO mapToCatalogueDTO(Machine machine) {
        MachineCatalogueDTO dto = new MachineCatalogueDTO();
        dto.setIdMachine(machine.getIdMachine());
        dto.setNom(machine.getNom());
        dto.setDescription(machine.getDescription());
        dto.setPrixJour(machine.getPrixJour());
        dto.setLocalisation(machine.getLocalisation());
        
        if (machine.getTypeMachine() != null) {
            dto.setTypeMachine(machine.getTypeMachine().getLibelle());
        }
        
        if (machine.getEtatMachine() != null) {
            dto.setEtatMachine(machine.getEtatMachine().getLibelle());
        }
        
        dto.setDisponible(machine.getDisponible());
        dto.setImageUrl(null); // À ajouter si vous avez des images
        
        return dto;
    }
}