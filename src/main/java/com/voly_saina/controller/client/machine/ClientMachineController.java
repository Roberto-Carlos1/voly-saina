package com.voly_saina.controller.client.machine;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voly_saina.dto.dtoMacine.MachineCatalogueDTO;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.TypeMachine;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.TypeMachineService;

@Controller
@RequestMapping("/catalogue/machines")
public class ClientMachineController {

    @Autowired
    private MachineService machineService;

    @Autowired
    private TypeMachineService typeMachineService;

    // ========== PAGES HTML ==========

    @GetMapping("/types")
    public String types() {
        return "client/machines/types";
    }

    @GetMapping("/catalogue")
    public String catalogue() {
        return "client/machines/catalogue";
    }

    @GetMapping("/detail")
    public String detail() {
        return "client/machines/detail";
    }
    // /client/machines/api/types
    @GetMapping("/api/types")
    @ResponseBody
    public ResponseEntity<List<TypeMachine>> getTypes() {
        List<TypeMachine> types = typeMachineService.findAll();
        return ResponseEntity.ok(types);
    }

    // /client/machines/api/catalogue
    @GetMapping("/api/catalogue")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getCatalogue() {
        List<Machine> machines = machineService.findAll();
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // /client/machines/api/disponibles
    @GetMapping("/api/disponibles")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getMachinesDisponibles() {
        List<Machine> machines = machineService.findAvailableMachines();
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // /client/machines/api/type/{typeId}
    @GetMapping("/api/type/{typeId}")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getMachinesByType(@PathVariable Long typeId) {
        List<Machine> machines = machineService.findByTypeMachine(typeId);
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 5. ⚠️ /client/machines/api/{id} - DOIT ÊTRE EN DERNIER
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<MachineCatalogueDTO> getMachineById(@PathVariable Long id) {
        Machine machine = machineService.findById(id);
        if (machine == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToCatalogueDTO(machine));
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
        dto.setImageUrl(null);
        
        return dto;
    }
}