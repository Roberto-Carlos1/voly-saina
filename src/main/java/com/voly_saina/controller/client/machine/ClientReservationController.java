// ClientReservationController.java
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.voly_saina.dto.dtoMacine.ReservationClientDTO;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.client.machine.ClientReservationService;

@Controller
@RequestMapping("/catalogue/reservations")
public class ClientReservationController {

    @Autowired
    private ClientReservationService clientReservationService;

    // ========== MÉTHODES D'AUTHENTIFICATION ==========

    private Utilisateur getUtilisateurConnecte(@AuthenticationPrincipal User user) {
        return clientReservationService.getUtilisateurConnecte(user);
    }

    private void addUtilisateurConnecte(Model model, @AuthenticationPrincipal User user) {
        clientReservationService.addUtilisateurConnecte(model, user);
    }

    // ========== PAGES HTML ==========

    @GetMapping("/{machineId}/nouvelle")
    public String formulaireReservation(
            @PathVariable Long machineId,
            @AuthenticationPrincipal User user,
            Model model) {

        addUtilisateurConnecte(model, user);
        model.addAttribute("machineId", machineId);
        return "client/reservations/form";
    }

    @GetMapping("/{id}/annuler")
    public String formulaireAnnulation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            Model model) {

        addUtilisateurConnecte(model, user);
        clientReservationService.annulerReservationByClient(id, getUtilisateurConnecte(user));
        return "redirect:/catalogue/reservations/mes-reservations";
    }

    @GetMapping("/mes-reservations")
    public String mesReservations(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String statut,
            Model model) {

        Utilisateur utilisateur = getUtilisateurConnecte(user);
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        addUtilisateurConnecte(model, user);

        var result = clientReservationService.getReservationsForClient(utilisateur.getIdUtilisateur(), statut);
        model.addAttribute("reservations", result.getReservations());
        model.addAttribute("filtreStatut", statut);
        return "client/reservations/list";
    }

    @GetMapping("/{id}")
    public String detailReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            Model model) {

        Utilisateur utilisateur = getUtilisateurConnecte(user);
        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        addUtilisateurConnecte(model, user);
        model.addAttribute("reservationId", id);
        return "client/reservations/detail";
    }

    @GetMapping("/facture/{factureId}")
    public String detailFacture(
            @PathVariable Long factureId,
            @AuthenticationPrincipal User user,
            Model model) {

        addUtilisateurConnecte(model, user);
        model.addAttribute("factureId", factureId);
        return "client/reservations/facture-detail";
    }

    // ========== API REST ==========

    @GetMapping("/api/client/{clientId}")
    @ResponseBody
    public ResponseEntity<List<ReservationClientDTO>> getReservationsByClient(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user) {

        if (!clientReservationService.isClientAuthorized(clientId, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(clientReservationService.getReservationsByClient(clientId));
    }

    @GetMapping("/api/client/{clientId}/statut/{statut}")
    @ResponseBody
    public ResponseEntity<List<ReservationClientDTO>> getReservationsByClientAndStatut(
            @PathVariable Long clientId,
            @PathVariable String statut,
            @AuthenticationPrincipal User user) {

        if (!clientReservationService.isClientAuthorized(clientId, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(clientReservationService.getReservationsByClientAndStatut(clientId, statut));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<ReservationClientDTO> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        ReservationClientDTO dto = clientReservationService.getReservationById(id, user);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/client/{clientId}/active")
    @ResponseBody
    public ResponseEntity<List<ReservationClientDTO>> getActiveReservations(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user) {

        if (!clientReservationService.isClientAuthorized(clientId, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(clientReservationService.getActiveReservations(clientId));
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReservation(
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal User user) {

        return clientReservationService.createReservation(payload, user);
    }

    @PostMapping("/api/{id}/facturer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> facturerReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return clientReservationService.facturerReservation(id, user);
    }

    @PostMapping("/api/facture/{factureId}/payer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> payerFacture(
            @PathVariable Long factureId,
            @RequestBody Map<String, Object> payload,
            @AuthenticationPrincipal User user) {

        return clientReservationService.payerFacture(factureId, payload, user);
    }

    @GetMapping("/api/facture/{factureId}/reservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReservationsByFacture(
            @PathVariable Long factureId,
            @AuthenticationPrincipal User user) {

        return clientReservationService.getReservationsByFacture(factureId, user);
    }

    @PutMapping("/api/{id}/annuler")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> annulerReservation(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload,
            @AuthenticationPrincipal User user) {

        return clientReservationService.annulerReservation(id, payload, user);
    }

    @PutMapping("/api/client/{clientId}/annuler-tout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> annulerTout(
            @PathVariable Long clientId,
            @AuthenticationPrincipal User user) {

        return clientReservationService.annulerTout(clientId, user);
    }
}