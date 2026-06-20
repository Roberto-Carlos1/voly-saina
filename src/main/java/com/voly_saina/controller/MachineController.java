package com.voly_saina.controller;

import com.voly_saina.entity.EtatMachine;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Pages;
import com.voly_saina.entity.TypeMachine;

import com.voly_saina.service.EtatMachineService;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.TypeMachineService;
import com.voly_saina.service.PageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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
    private final PageService pageService;

    public MachineController(EtatMachineService etatMachineService, MachineService machineService,
            TypeMachineService typeMachineService, PageService pageService) {
        this.etatMachineService = etatMachineService;
        this.machineService = machineService;
        this.typeMachineService = typeMachineService;
        this.pageService = pageService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // GET /api/machines
    @GetMapping
    public String getAll(@RequestParam(defaultValue = "0") int page, Model model) {
        List<EtatMachine> etats = etatMachineService.findAll();
        List<TypeMachine> types = typeMachineService.findAll();

        Pages config = pageService.getConfiguration();
        int size = config.getNombre();

        Pageable pageable = PageRequest.of(page, size);

        Page<Machine> machinePage = machineService.findByPage(pageable);

        model.addAttribute("machines", machinePage.getContent()); 
        model.addAttribute("etatMachine", etats);
        model.addAttribute("types", types);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", machinePage.getTotalPages());

        return "machines/list";
    }

    @PostMapping("/pages")
    public String nombrePages(@RequestParam("pages") int page, Model model){
        Pages p = pageService.findById(1L);
        p.setNombre(page);
        pageService.save(p);
        return "redirect:/api/machines" ;
    }

    // GET /api/machines/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Machine> getById(@PathVariable Long id) {
        return machineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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
