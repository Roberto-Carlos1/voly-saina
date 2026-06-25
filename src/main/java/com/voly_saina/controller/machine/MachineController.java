package com.voly_saina.controller.machine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.voly_saina.entity.EtatMachine;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Pages;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.StatutMachine;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.TypeMachine;
import com.voly_saina.entity.MaintenanceMachine;
import com.voly_saina.entity.StatutMaintenance;
import com.voly_saina.service.EtatMachineService;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.MaintenanceMachineService;
import com.voly_saina.service.PageService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.StatutMachineService;
import com.voly_saina.service.TypeMachineService;

@Controller
@RequestMapping("/api/machines")
public class MachineController {
    private final EtatMachineService etatMachineService;
    private final MachineService machineService;
    private final TypeMachineService typeMachineService;
    private final MaintenanceMachineService maintenanceMachineService;
    private final ReservationMachineService reservationMachineService;
    private final PageService pageService;
    private final StatutMachineService statutMachineService;

    public MachineController(EtatMachineService etatMachineService, MachineService machineService,
            TypeMachineService typeMachineService, MaintenanceMachineService maintenanceMachineService,
            ReservationMachineService reservationMachineService, PageService pageService,
            StatutMachineService statutMachineService) {
        this.etatMachineService = etatMachineService;
        this.machineService = machineService;
        this.typeMachineService = typeMachineService;
        this.maintenanceMachineService = maintenanceMachineService;
        this.reservationMachineService = reservationMachineService;
        this.pageService = pageService;
        this.statutMachineService = statutMachineService;

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
        List<StatutMachine> status = statutMachineService.findAll();

        Pages config = pageService.getConfiguration();
        int size = config.getNombre();

        Pageable pageable = PageRequest.of(page, size);

        Page<Machine> machinePage = machineService.findByPage(pageable);

        List<Machine> machines = machinePage.getContent();

        Map<Long, StatutMachine> derniersStatuts = new HashMap<>();

        for (Machine m : machines) {
            StatutMachine actuel = statutMachineService.findCurrentByMachineId(m.getIdMachine());
            if (actuel != null) {
                derniersStatuts.put(m.getIdMachine(), actuel);
            }
        }

        model.addAttribute("machines", machines);
        model.addAttribute("etatMachine", etats);
        model.addAttribute("types", types);
        model.addAttribute("derniersStatuts", derniersStatuts);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", machinePage.getTotalPages());
        return "machines/list";
    }

    @PostMapping("/pages")
    public String nombrePages(@RequestParam("pages") int page, RedirectAttributes attributes) {
        Pages p = pageService.findById(1L);
        if (page <= 0) {
            attributes.addFlashAttribute("error", "Entrez un nombre de pages valide");
            return "redirect:/api/machines";
        } else {
            p.setNombre(page);
            pageService.save(p);
        }
        return "redirect:/api/machines";
    }

    @GetMapping("/view/insert")
    public String insertMachine(Model model) {
        List<EtatMachine> etats = etatMachineService.findAll();
        List<TypeMachine> types = typeMachineService.findAll();
        model.addAttribute("etatMachine", etats);
        model.addAttribute("types", types);
        return "machines/insert-machine";
    }

    @PostMapping("/insert")
    public String insertMachine(@RequestParam("nom") String nom,
            @RequestParam("typeMachine") Long typeMachineId,
            @RequestParam("description") String description,
            @RequestParam("prixJour") String prixJour,
            @RequestParam("etatMachine") Long etatMachine,
            @RequestParam("localisation") String localisation,
            @RequestParam("kilometrage") String kilometrage,
            @RequestParam(value = "disponible", defaultValue = "true") Boolean disponible,
            RedirectAttributes redirectAttributes) {
        Machine machine = new Machine();
        machine.setNom(nom);
        machine.setTypeMachine(typeMachineService.findById(typeMachineId));
        machine.setDescription(description);
        machine.setPrixJour(new java.math.BigDecimal(prixJour));
        machine.setLocalisation(localisation);
        machine.setKilometrage(new java.math.BigDecimal(kilometrage));
        machine.setDisponible(disponible);
        Machine savedMachine = machineService.save(machine);

        StatutMachine statutMachine = new StatutMachine();
        statutMachine.setMachine(savedMachine);
        statutMachine.setEtatMachine(etatMachineService.findById(etatMachine));
        statutMachineService.save(statutMachine);

        redirectAttributes.addFlashAttribute("success", "Machine insérée avec succès");
        return "redirect:/api/machines";
    }

    @GetMapping("/delete/{id}")
    public String deleteMachine(@PathVariable Long id, Model model) {
        machineService.deleteById(id);
        return "redirect:/api/machines";
    }

    @GetMapping("/modify/{id}")
    public String modifyMachine(@PathVariable Long id, Model model) {
        Machine m = machineService.findById(id);
        List<EtatMachine> etats = etatMachineService.findAll();
        List<TypeMachine> types = typeMachineService.findAll();
        model.addAttribute("machine", m);
        model.addAttribute("etatMachine", etats);
        model.addAttribute("types", types);
        return "machines/modify-machine";
    }

    @PostMapping("/modify/{id}")
    public String updateMachine(@PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("typeMachine") Long typeMachineId,
            @RequestParam("description") String description,
            @RequestParam("prixJour") String prixJour,
            @RequestParam("etatMachine") Long etatMachineId,
            @RequestParam("localisation") String localisation,
            @RequestParam("kilometrage") String kilometrage,
            @RequestParam(value = "disponible", defaultValue = "false") Boolean disponible,
            RedirectAttributes redirectAttributes) {
        Machine machine = machineService.findById(id);
        if (machine == null) {
            redirectAttributes.addFlashAttribute("error", "Machine introuvable");
            return "redirect:/api/machines";
        }

        machine.setNom(nom);
        machine.setTypeMachine(typeMachineService.findById(typeMachineId));
        machine.setDescription(description);
        machine.setPrixJour(new java.math.BigDecimal(prixJour));
        machine.setLocalisation(localisation);
        machine.setKilometrage(new java.math.BigDecimal(kilometrage));
        machine.setDisponible(disponible);
        StatutMachine currentStatut = statutMachineService.findCurrentByMachineId(id);
        EtatMachine currentEtat = currentStatut == null ? null : currentStatut.getEtatMachine();
        Machine savedMachine = machineService.save(machine);

        if (currentEtat == null || !currentEtat.getIdEtatMachine().equals(etatMachineId)) {
            StatutMachine statutMachine = new StatutMachine();
            statutMachine.setMachine(savedMachine);
            statutMachine.setEtatMachine(etatMachineService.findById(etatMachineId));
            statutMachineService.save(statutMachine);
        }

        redirectAttributes.addFlashAttribute("success", "Machine modifiée avec succès");
        return "redirect:/api/machines";
    }

    // GET /api/machines/{id}
    @GetMapping("/{id}")
    public String getMachinebyId(@PathVariable Long id, Model model) {
        Machine m = machineService.findById(id);
        model.addAttribute("machine", m);

        List<ReservationMachine> reservation = reservationMachineService.findMachine(id);
        model.addAttribute("reservations", reservation);
        List<MaintenanceMachine> maintenance = maintenanceMachineService.findByMachine(m);
        model.addAttribute("maintenances", maintenance);
        return "machines/detail-machine";
    }

    @PostMapping("/filtre")
    public ResponseEntity<Page<Machine>> filtreMachine(@RequestParam("nomMachine") String name,
            @RequestParam("typeMachine") String typeID,
            @RequestParam("etatMachine") String etatID,
            @RequestParam("page") int page) {

        // List<Machine> liste = machineService.filtrerMachine(typeID, name);
        Pages config = pageService.getConfiguration();
        Pageable pageable = PageRequest.of(page, config.getNombre());
        
        Page<Machine> machines = machineService.filtrerMachine(typeID, etatID, name, pageable);
        return ResponseEntity.ok(machines);
    }
}
