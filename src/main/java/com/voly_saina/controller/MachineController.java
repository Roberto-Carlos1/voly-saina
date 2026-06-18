package com.voly_saina.controller;

import com.voly_saina.entity.EtatMachine;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.TypeMachine;
import com.voly_saina.service.EtatMachineService;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.TypeMachineService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/machines")
public class MachineController {
    private final EtatMachineService etatMachineService;
    private final MachineService machineService;
    private final TypeMachineService typeMachineService;
    private final ReservationMachineService reservationMachineService;

    public MachineController(EtatMachineService etatMachineService, MachineService machineService,
            TypeMachineService typeMachineService, ReservationMachineService reservationMachineService) {
        this.etatMachineService = etatMachineService;
        this.machineService = machineService;
        this.typeMachineService = typeMachineService;
        this.reservationMachineService= reservationMachineService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // GET /api/machines
    @GetMapping
    public String getAll(Model model) {
        List<EtatMachine> etats = etatMachineService.findAll();
        List<TypeMachine> types = typeMachineService.findAll();
        List<Machine> liste = machineService.findAll();
        
        model.addAttribute("machines", liste);
        model.addAttribute("etatMachine", etats);
        model.addAttribute("types", types);

        return "machines/list";
    }

    // GET /api/machines/{id}
    @GetMapping("/{id}")

    public String getMachinebyId(@PathVariable Long id, Model model){
        Machine m= machineService.findById(id);
        model.addAttribute("machine", m);

        List<ReservationMachine> reservation= reservationMachineService.findMachine(id); 
        model.addAttribute("reservations", reservation);

        return "machines/detail-machine";
    }

    // POST /api/machines
    @PostMapping
    public ResponseEntity<Machine> create(@RequestBody Machine machine) {
        Machine saved = machineService.save(machine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/machines/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Machine> update(@PathVariable Long id, @RequestBody Machine machine) {
        if (!machineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        machine.setIdMachine(id);
        Machine updated = machineService.save(machine);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/machines/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!machineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        machineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
