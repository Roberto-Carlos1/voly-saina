package com.voly_saina.controller.machine;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.entity.Pages;
import com.voly_saina.entity.StatutMaintenance;
import com.voly_saina.repository.MachineRepository;
import com.voly_saina.service.MachineService;
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
@RequestMapping("/admin/maintenances-machine")
public class MaintenanceMachineController {

    private final MachineRepository machineRepository;
    private final MachineService machineService;
    private final PageService pageService;
    private final StatutMaintenanceService statutMaintenanceService;

    @Autowired
    private MaintenanceMachineService maintenanceMachineService;

    MaintenanceMachineController(MachineRepository machineRepository, MachineService machineService, PageService pageService, StatutMaintenanceService statutMaintenanceService) {
        this.machineRepository = machineRepository;
        this.machineService = machineService;
        this.pageService = pageService;
        this.statutMaintenanceService = statutMaintenanceService;
    }

    // GET /maintenances-machine
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
            return "redirect:/admin/maintenances-machine";
        } else {
            p.setNombre(page);
            pageService.save(p);
        }
        return "redirect:/admin/maintenances-machine";
    }

    @PostMapping("/api/filtre")
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

    // ==================== FORM VIEWS ====================

    @GetMapping("/ajouter")
    public String insertForm(Model model) {
        List<Machine> machines = machineService.findAll();
        List<StatutMaintenance> statuts = statutMaintenanceService.findAll();
        model.addAttribute("machines", machines);
        model.addAttribute("statuts", statuts);
        model.addAttribute("action", "insert");
        return "/machines/maintenance/form";
    }

    @GetMapping("/modifier/{id}")
    public String modifyForm(@PathVariable Long id, Model model) {
        MaintenanceMachine maintenance = maintenanceMachineService.findById(id).orElse(null);
        if (maintenance == null) {
            return "redirect:/admin/maintenances-machine";
        }
        List<Machine> machines = machineService.findAll();
        List<StatutMaintenance> statuts = statutMaintenanceService.findAll();
        model.addAttribute("maintenance", maintenance);
        model.addAttribute("machines", machines);
        model.addAttribute("statuts", statuts);
        model.addAttribute("action", "update");
        return "/machines/maintenance/form";
    }

    @PostMapping("/ajouter")
    public String insert(@RequestParam("idMachine") Long idMachine,
                         @RequestParam("dateCreation") String dateCreation,
                         @RequestParam("travaux") String travaux,
                         @RequestParam("cout") String cout,
                         RedirectAttributes redirectAttributes) {
        if (idMachine == null || dateCreation == null || dateCreation.isEmpty()
                || travaux == null || travaux.trim().isEmpty()
                || cout == null || cout.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tous les champs sont obligatoires");
            return "redirect:/admin/maintenances-machine/ajouter";
        }
        if (!dateCreation.matches("\\d{4}-\\d{2}-\\d{2}")) {
            redirectAttributes.addFlashAttribute("error", "Format de date invalide (AAAA-MM-JJ)");
            return "redirect:/admin/maintenances-machine/ajouter";
        }
        double coutVal;
        try {
            coutVal = Double.parseDouble(cout);
            if (coutVal < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Le coût doit être un nombre positif");
            return "redirect:/admin/maintenances-machine/ajouter";
        }
        if (machineService.findById(idMachine) == null) {
            redirectAttributes.addFlashAttribute("error", "Machine introuvable");
            return "redirect:/admin/maintenances-machine/ajouter";
        }

        MaintenanceMachine saved = maintenanceMachineService.createMaintenanceMachine(
                idMachine, travaux.trim(), coutVal, LocalDate.parse(dateCreation));
        redirectAttributes.addFlashAttribute("success", "Maintenance créée avec succès");
        return "redirect:/admin/maintenances-machine";
    }

    @PostMapping("/modifier/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam("idMachine") Long idMachine,
                         @RequestParam("dateDebut") String dateDebut,
                         @RequestParam("dateRetourPrevue") String dateRetourPrevue,
                         @RequestParam("travaux") String travaux,
                         @RequestParam("cout") String cout,
                         @RequestParam(value = "idStatutMaintenance", required = false) Long idStatutMaintenance,
                         RedirectAttributes redirectAttributes) {
        MaintenanceMachine maintenance = maintenanceMachineService.findById(id).orElse(null);
        if (maintenance == null) {
            redirectAttributes.addFlashAttribute("error", "Maintenance introuvable");
            return "redirect:/admin/maintenances-machine";
        }

        if (travaux == null || travaux.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Le champ travaux est obligatoire");
            return "redirect:/admin/maintenances-machine/modifier/" + id;
        }
        double coutVal;
        try {
            coutVal = Double.parseDouble(cout);
            if (coutVal < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Le coût doit être un nombre positif");
            return "redirect:/admin/maintenances-machine/modifier/" + id;
        }
        Machine machine = machineService.findById(idMachine);
        if (machine == null) {
            redirectAttributes.addFlashAttribute("error", "Machine introuvable");
            return "redirect:/admin/maintenances-machine/modifier/" + id;
        }

        maintenance.setMachine(machine);
        maintenance.setTravaux(travaux.trim());
        maintenance.setCout(BigDecimal.valueOf(coutVal));
        if (dateDebut != null && !dateDebut.isEmpty()) {
            maintenance.setDateDebut(LocalDate.parse(dateDebut));
        }
        if (dateRetourPrevue != null && !dateRetourPrevue.isEmpty()) {
            maintenance.setDateRetourPrevue(LocalDate.parse(dateRetourPrevue));
        }
        if (idStatutMaintenance != null) {
            statutMaintenanceService.findById(idStatutMaintenance)
                    .ifPresent(maintenance::setStatutMaintenance);
        }

        maintenanceMachineService.save(maintenance);
        redirectAttributes.addFlashAttribute("success", "Maintenance modifiée avec succès");
        return "redirect:/admin/maintenances-machine";
    }

    @GetMapping("/supprimer/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!maintenanceMachineService.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Maintenance introuvable");
        } else {
            maintenanceMachineService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Maintenance supprimée avec succès");
        }
        return "redirect:/admin/maintenances-machine";
    }

    // ==================== API REST ====================

    // GET /maintenances-machine/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceMachine> getById(@PathVariable Long id) {
        return maintenanceMachineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /maintenances-machine
    @PostMapping
    public ResponseEntity<MaintenanceMachine> create(@RequestParam("idMachine") Long idMachine,
            @RequestParam("dateCreation") LocalDate dateCreation, @RequestParam("travaux") String travaux,
            @RequestParam("cout") double cout) {

        MaintenanceMachine saved = maintenanceMachineService.createMaintenanceMachine(idMachine, travaux, cout,
                dateCreation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/valider/{id}")
    public String validateForm(@PathVariable Long id, Model model) {
        MaintenanceMachine maintenance = maintenanceMachineService.findById(id).orElse(null);
        if (maintenance == null) {
            return "redirect:/admin/maintenances-machine";
        }
        model.addAttribute("maintenance", maintenance);
        return "/machines/maintenance/validation";
    }

    @PostMapping("/valider")
    public String validate(@RequestParam("idMaintenance") Long idMaintenance,
                           @RequestParam("dateRetourReelle") String dateRetourReelle,
                           RedirectAttributes redirectAttributes) {
        MaintenanceMachine maintenance = maintenanceMachineService.findById(idMaintenance).orElse(null);
        if (maintenance == null) {
            redirectAttributes.addFlashAttribute("error", "Maintenance introuvable");
            return "redirect:/admin/maintenances-machine";
        }
        if (dateRetourReelle == null || dateRetourReelle.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La date de retour réelle est obligatoire");
            return "redirect:/admin/maintenances-machine/valider/" + idMaintenance;
        }
        if (!dateRetourReelle.matches("\\d{4}-\\d{2}-\\d{2}")) {
            redirectAttributes.addFlashAttribute("error", "Format de date invalide (AAAA-MM-JJ)");
            return "redirect:/admin/maintenances-machine/valider/" + idMaintenance;
        }
        try {
            maintenanceMachineService.validateMaintenanceMachine(
                    idMaintenance, maintenance.getMachine().getIdMachine(), LocalDate.parse(dateRetourReelle));
            redirectAttributes.addFlashAttribute("success", "Maintenance validée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la validation: " + e.getMessage());
        }
        return "redirect:/admin/maintenances-machine";
    }

    // PUT /maintenances-machine/{id}
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

    // DELETE /maintenances-machine/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!maintenanceMachineService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        maintenanceMachineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
