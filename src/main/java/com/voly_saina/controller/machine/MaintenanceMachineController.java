package com.voly_saina.controller.machine;

import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.repository.MachineRepository;
import com.voly_saina.service.MaintenanceMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/maintenances-machine")
public class MaintenanceMachineController {

    private final MachineRepository machineRepository;
    @Autowired
    private MaintenanceMachineService maintenanceMachineService;

    MaintenanceMachineController(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    // GET /api/maintenances-machine
    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("maintenances", maintenanceMachineService.findAll());
        return "/machines/maintenance/list";
    }

    // GET /api/maintenances-machine/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceMachine> getById(@PathVariable Long id) {
        return maintenanceMachineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/maintenances-machine
    @PostMapping
    public ResponseEntity<MaintenanceMachine> create(@RequestParam("idMachine") Long idMachine,
            @RequestParam("dateCreation") LocalDate dateCreation, @RequestParam("travaux") String travaux,
            @RequestParam("cout") double cout) {

        MaintenanceMachine saved = maintenanceMachineService.createMaintenanceMachine(idMachine, travaux, cout,
                dateCreation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // POST /api/maintenances-machine/validate
    @PostMapping("/validate")
    public ResponseEntity<MaintenanceMachine> validate(@RequestParam("idMaintenance") Long idMaintenance,
            @RequestParam("idMachine") Long idMachine, @RequestParam("dateRetourReelle") LocalDate dateRetourReelle) {
        MaintenanceMachine validated = maintenanceMachineService.validateMaintenanceMachine(idMaintenance, idMachine,
                dateRetourReelle);
        return ResponseEntity.ok(validated);
    }

    // PUT /api/maintenances-machine/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceMachine> update(@PathVariable Long id,
            @RequestBody MaintenanceMachine maintenanceMachine) {
        if (!maintenanceMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        maintenanceMachine.setIdMaintenance(id);
        MaintenanceMachine updated = maintenanceMachineService.save(maintenanceMachine);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/maintenances-machine/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!maintenanceMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        maintenanceMachineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
