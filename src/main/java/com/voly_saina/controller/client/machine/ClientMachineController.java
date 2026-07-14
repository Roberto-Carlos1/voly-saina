// ClientMachineController.java
package com.voly_saina.controller.client.machine;

import java.util.List;

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
import com.voly_saina.service.client.machine.ClientMachineService;

@Controller
@RequestMapping("/catalogue/machines")
public class ClientMachineController {

    @Autowired
    private MachineService machineService;

    @Autowired
    private TypeMachineService typeMachineService;

    @Autowired
    private ClientMachineService clientMachineService;

    // ========== MÉTHODES D'AUTHENTIFICATION ==========

    private Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        return clientMachineService.getUtilisateurConnecte(user);
    }

    private void addUtilisateurConnecte(Model model, @AuthenticationPrincipal User user) {
        clientMachineService.addUtilisateurConnecte(model, user);
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
        
        // Déléguer la logique métier au service
        var result = clientMachineService.getCatalogueWithFilters(typeId, disponible, prixMax, page);
        
        model.addAttribute("machines", result.getMachines());
        model.addAttribute("types", typeMachineService.findAll());
        model.addAttribute("currentPage", result.getCurrentPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("disponibleOnly", disponible != null && disponible);
        model.addAttribute("prixMax", prixMax);
        
        return "client/machines/catalogue";
    }

    @GetMapping("/detail")
    public String detail(
            @AuthenticationPrincipal User user,
            @RequestParam(required = true) Long id,
            Model model) {
        
        addUtilisateurConnecte(model, user);
        
        Machine machine = clientMachineService.getMachineDetail(id);
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
        return ResponseEntity.ok(typeMachineService.findAll());
    }

    @GetMapping("/api/catalogue")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getCatalogue() {
        return ResponseEntity.ok(clientMachineService.getAllMachinesForCatalogue());
    }

    @GetMapping("/api/disponibles")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getMachinesDisponibles() {
        return ResponseEntity.ok(clientMachineService.getAvailableMachinesForCatalogue());
    }

    @GetMapping("/api/type/{typeId}")
    @ResponseBody
    public ResponseEntity<List<MachineCatalogueDTO>> getMachinesByType(@PathVariable Long typeId) {
        return ResponseEntity.ok(clientMachineService.getMachinesByTypeForCatalogue(typeId));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<MachineCatalogueDTO> getMachineById(@PathVariable Long id) {
        MachineCatalogueDTO dto = clientMachineService.getMachineCatalogueDTOById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
}