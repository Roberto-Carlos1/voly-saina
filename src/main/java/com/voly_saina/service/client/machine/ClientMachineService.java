// ClientMachineService.java
package com.voly_saina.service.client.machine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.voly_saina.dto.dtoMacine.MachineCatalogueDTO;
import com.voly_saina.entity.Machine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.UtilisateurService;

@Service
public class ClientMachineService {

    @Autowired
    private MachineService machineService;

    @Autowired
    private UtilisateurService utilisateurService;

    // ========== AUTHENTIFICATION ==========

    public Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        if (user == null) {
            return null;
        }
        return utilisateurService.findByEmail(user.getUsername());
    }

    public void addUtilisateurConnecte(Model model, @AuthenticationPrincipal User user) {
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

    // ========== CATALOGUE ==========

    public CatalogueResult getCatalogueWithFilters(Long typeId, Boolean disponible, Integer prixMax, int page) {
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
        
        CatalogueResult result = new CatalogueResult();
        result.setMachines(pageMachines);
        result.setCurrentPage(page);
        result.setTotalPages(totalPages);
        return result;
    }

    public Machine getMachineDetail(Long id) {
        return machineService.findById(id);
    }

    public List<MachineCatalogueDTO> getAllMachinesForCatalogue() {
        List<Machine> machines = machineService.findAll();
        return machines.stream().map(this::mapToCatalogueDTO).collect(Collectors.toList());
    }

    public List<MachineCatalogueDTO> getAvailableMachinesForCatalogue() {
        List<Machine> machines = machineService.findAvailableMachines();
        return machines.stream().map(this::mapToCatalogueDTO).collect(Collectors.toList());
    }

    public List<MachineCatalogueDTO> getMachinesByTypeForCatalogue(Long typeId) {
        List<Machine> machines = machineService.findByTypeMachine(typeId);
        return machines.stream().map(this::mapToCatalogueDTO).collect(Collectors.toList());
    }

    public MachineCatalogueDTO getMachineCatalogueDTOById(Long id) {
        Machine machine = machineService.findById(id);
        if (machine == null) {
            return null;
        }
        return mapToCatalogueDTO(machine);
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

    // ========== CLASSES INTERNES ==========

    public static class CatalogueResult {
        private List<Machine> machines;
        private int currentPage;
        private int totalPages;

        public List<Machine> getMachines() { return machines; }
        public void setMachines(List<Machine> machines) { this.machines = machines; }
        public int getCurrentPage() { return currentPage; }
        public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}