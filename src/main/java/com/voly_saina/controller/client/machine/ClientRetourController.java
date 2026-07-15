// ClientRetourController.java
package com.voly_saina.controller.client.machine;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voly_saina.dto.dtoMacine.FormulaireRetourDTO;
import com.voly_saina.dto.dtoMacine.RetourClientDTO;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.client.machine.ClientRetourService;

@Controller
@RequestMapping("/catalogue/retours")
public class ClientRetourController {

    @Autowired
    private ClientRetourService clientRetourService;

    // ========== MÉTHODES D'AUTHENTIFICATION ==========

    private Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        return clientRetourService.getUtilisateurConnecte(user);
    }

    private void addUtilisateurConnecte(Model model, @AuthenticationPrincipal User user) {
        clientRetourService.addUtilisateurConnecte(model, user);
    }

    // ========== PAGES HTML ==========

    @GetMapping("/{reservationId}/nouveau")
    public String formulaireRetour(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal User user,
            Model model) {
        
        addUtilisateurConnecte(model, user);
        model.addAttribute("reservationId", reservationId);
        return "client/retours/form";
    }

    // ========== API REST ==========

    @GetMapping("/api/form/{reservationId}")
    @ResponseBody
    public ResponseEntity<?> getFormulaireRetour(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal User user) {
        
        return clientRetourService.getFormulaireRetour(reservationId, user);
    }

    @GetMapping("/api/reservation/{reservationId}")
    @ResponseBody
    public ResponseEntity<?> getRetourByReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal User user) {
        
        return clientRetourService.getRetourByReservation(reservationId, user);
    }

    @GetMapping("/api/client/{clientId}")
    @ResponseBody
    public ResponseEntity<List<RetourClientDTO>> getRetoursByClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user) {
        
        if (!clientRetourService.isClientAuthorized(clientId, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(clientRetourService.getRetoursByClient(clientId));
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> enregistrerRetour(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal User user) {
        
        return clientRetourService.enregistrerRetour(payload, user);
    }

    @GetMapping("/api/penalite/{reservationId}")
    @ResponseBody
    public ResponseEntity<?> getPenalite(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal User user) {
        
        return clientRetourService.getPenalite(reservationId, user);
    }
}