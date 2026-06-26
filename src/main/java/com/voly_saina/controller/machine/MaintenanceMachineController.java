package com.voly_saina.controller.machine;

import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.entity.Pages;
import com.voly_saina.entity.StatutMaintenance;
import com.voly_saina.repository.MachineRepository;
import com.voly_saina.service.MaintenanceMachineService;
import com.voly_saina.service.PageService;
import com.voly_saina.service.StatutMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/api/maintenances-machine")
public class MaintenanceMachineController {

    private final MachineRepository machineRepository;
    private final PageService pageService;
    private final StatutMaintenanceService statutMaintenanceService;

    @Autowired
    private MaintenanceMachineService maintenanceMachineService;

    MaintenanceMachineController(MachineRepository machineRepository, PageService pageService, StatutMaintenanceService statutMaintenanceService) {
        this.machineRepository = machineRepository;
        this.pageService = pageService;
        this.statutMaintenanceService = statutMaintenanceService;
    }

    // GET /api/maintenances-machine
    @GetMapping
    public String getAll(@RequestParam(defaultValue = "0") int page, Model model) {
        Pages config = pageService.getConfiguration();
        int size = config.getNombre();
        Pageable pageable = PageRequest.of(page, size);
        Page<MaintenanceMachine> maintenancePage = maintenanceMachineService.findAllByPage(pageable);

        List<StatutMaintenance> statuts = statutMaintenanceService.findAll();

        model.addAttribute("maintenances", maintenancePage.getContent());
        model.addAttribute("statuts", statuts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", maintenancePage.getTotalPages());
        return "/machines/maintenance/list";
    }

    @PostMapping("/pages")
    public String nombrePages(@RequestParam("pages") int page, RedirectAttributes attributes) {
        Pages p = pageService.findById(1L);
        if (page <= 0) {
            attributes.addFlashAttribute("error", "Entrez un nombre de pages valide");
            return "redirect:/api/maintenances-machine";
        } else {
            p.setNombre(page);
            pageService.save(p);
        }
        return "redirect:/api/maintenances-machine";
    }

    @PostMapping("/filtre")
    public ResponseEntity<Page<MaintenanceMachine>> filtreMaintenance(
            @RequestParam(value = "idMachine", required = false) String idMachine,
            @RequestParam(value = "codeStatut", required = false) String codeStatut,
            @RequestParam("page") int page) {
        Pages config = pageService.getConfiguration();
        Pageable pageable = PageRequest.of(page, config.getNombre());

        Page<MaintenanceMachine> maintenances;
        boolean hasMachine = idMachine != null && !idMachine.isEmpty();
        boolean hasStatut = codeStatut != null && !codeStatut.isEmpty();

        if (hasMachine && hasStatut) {
            maintenances = maintenanceMachineService.findByMachineAndStatutCode(Long.parseLong(idMachine), codeStatut, pageable);
        } else if (hasMachine) {
            maintenances = maintenanceMachineService.findByMachineId(Long.parseLong(idMachine), pageable);
        } else if (hasStatut) {
            maintenances = maintenanceMachineService.findByStatutCode(codeStatut, pageable);
        } else {
            maintenances = maintenanceMachineService.findAllByPage(pageable);
        }

        return ResponseEntity.ok(maintenances);
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
