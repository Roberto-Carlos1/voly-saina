package com.voly_saina.controller.client.machine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voly_saina.dto.dtoMacine.MachineCatalogueDTO;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.TypeMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.TypeMachineService;
import com.voly_saina.service.client.ClientProfilService;

@Controller
@RequestMapping("/catalogue/machines")
public class ClientMachineController {

    @Autowired
    private MachineService machineService;

    @Autowired
    private TypeMachineService typeMachineService;

    @Autowired
    private ClientProfilService clientProfilService;

    // ========== MÉTHODES D'AUTHENTIFICATION ==========

    private Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        if (user == null) {
            return null;
        }
        return clientProfilService.getUtilisateurByEmail(user.getUsername());
    }

    private void addUtilisateurConnecte(Model model, @AuthenticationPrincipal User user) {
        Utilisateur utilisateur = getUtilisateurConnecte(user);
        if (utilisateur != null) {
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("clientId", utilisateur.getIdUtilisateur());
            model.addAttribute("idClient", utilisateur.getIdUtilisateur());
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("clientId", null);
        }
    }

    // ========== PAGES HTML ==========

    @GetMapping("/types")
    public String types(@AuthenticationPrincipal User user, Model model) {
        addUtilisateurConnecte(model, user);
        return "client/machines/types";
    }

    @GetMapping("/catalogue")
    public String catalogue(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) Integer prixMax,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        
        addUtilisateurConnecte(model, user);
        
        List<Machine> machines = machineService.findAll();
        
        // Appliquer les filtres
        if (typeId != null) {
            machines = machines.stream()
                .filter(m -> m.getTypeMachine() != null && 
                            m.getTypeMachine().getIdTypeMachine().equals(typeId))
                .collect(Collectors.toList());
        }
        
        if (disponible != null && disponible) {
            machines = machines.stream()
                .filter(m -> m.getDisponible() != null && m.getDisponible())
                .collect(Collectors.toList());
        }
        
        if (prixMax != null) {
            machines = machines.stream()
                .filter(m -> m.getPrixJour() != null && 
                            m.getPrixJour().compareTo(BigDecimal.valueOf(prixMax)) <= 0)
                .collect(Collectors.toList());
        }
        
        // Pagination
        int pageSize = 9;
        int total = machines.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int start = page * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Machine> pageMachines = start < total ? machines.subList(start, end) : new ArrayList<>();
        
        model.addAttribute("machines", pageMachines);
        model.addAttribute("types", typeMachineService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("disponibleOnly", disponible != null && disponible);
        model.addAttribute("prixMax", prixMax);
        
        return "client/machines/catalogue";
    }

    // ⚠️ MÉTHODE CORRIGÉE - avec @RequestParam Long id
    @GetMapping("/detail")
    public String detail(
            @AuthenticationPrincipal User user,
            @RequestParam(required = true) Long id,
            Model model) {
        
        addUtilisateurConnecte(model, user);
        
        // Récupérer la machine par son ID
        Machine machine = machineService.findById(id);
        
        if (machine == null) {
            return "redirect:/catalogue/machines/catalogue";
        }
        
        model.addAttribute("machine", machine);
        model.addAttribute("clientId", model.getAttribute("clientId"));
        
        return "client/machines/detail";
    }

    // ========== API REST ==========

    @GetMapping("/api/types")
    @ResponseBody
    public ResponseEntity<List<TypeMachine>> getTypes() {
        List<TypeMachine> types = typeMachineService.findAll();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/api/catalogue")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getCatalogue() {
        List<Machine> machines = machineService.findAll();
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/disponibles")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getMachinesDisponibles() {
        List<Machine> machines = machineService.findAvailableMachines();
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/type/{typeId}")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getMachinesByType(@PathVariable Long typeId) {
        List<Machine> machines = machineService.findByTypeMachine(typeId);
        List<MachineCatalogueDTO> response = machines.stream()
            .map(this::mapToCatalogueDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<MachineCatalogueDTO> getMachineById(@PathVariable Long id) {
        Machine machine = machineService.findById(id);
        if (machine == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToCatalogueDTO(machine));
    }

    // ========== MÉTHODES PRIVÉES ==========

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