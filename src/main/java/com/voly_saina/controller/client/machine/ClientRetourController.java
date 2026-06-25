package com.voly_saina.controller.client.machine;

import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.RetourMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.RetourMachineService;
import com.voly_saina.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/client/retours")
public class ClientRetourController {

    @Autowired
    private ReservationMachineService reservationService;
    
    @Autowired
    private RetourMachineService retourService;
    
    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping("/{reservationId}/nouveau")
    public String formulaireRetour(@PathVariable Long reservationId,
                                   @RequestParam(required = false) Long clientId,
                                   Model model) {
        Long idClient = clientId != null ? clientId : 1L;
        Utilisateur client = utilisateurService.findById(idClient)
            .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        
        ReservationMachine reservation = reservationService.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        if (!reservation.getClient().getIdUtilisateur().equals(client.getIdUtilisateur())) {
            model.addAttribute("error", "Vous n'êtes pas autorisé");
            return "client/machines/error";
        }
        
        String statut = reservation.getStatutReservation().getCode();
        if (!"en_cours".equals(statut) && !"validee".equals(statut)) {
            model.addAttribute("error", "Cette réservation ne peut pas être retournée");
            return "client/machines/error";
        }
        
        if (reservation.getRetour() != null) {
            model.addAttribute("error", "Retour déjà enregistré");
            return "client/machines/error";
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("clientId", clientId);
        return "client/retours/form";
    }

    @PostMapping("/{reservationId}/nouveau")
    public String enregistrerRetour(@PathVariable Long reservationId,
                                    @RequestParam String etatRetour,
                                    @RequestParam(required = false) String remarque,
                                    @RequestParam(required = false) Long clientId,
                                    Model model) {
        try {
            Long idClient = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            
            ReservationMachine reservation = reservationService.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (!reservation.getClient().getIdUtilisateur().equals(client.getIdUtilisateur())) {
                model.addAttribute("error", "Vous n'êtes pas autorisé");
                return "client/machines/error";
            }

            RetourMachine retour = new RetourMachine();
            retour.setReservation(reservation);
            retour.setDateRetour(LocalDate.now());
            retour.setEtatRetour(etatRetour);
            retour.setRemarque(remarque);

            RetourMachine saved = retourService.enregistrerRetour(retour);

            model.addAttribute("retour", saved);
            model.addAttribute("reservation", reservation);
            model.addAttribute("message", "Retour enregistré avec succès");
            
            long joursRetard = ChronoUnit.DAYS.between(reservation.getDateFin(), LocalDate.now());
            if (joursRetard > 0) {
                model.addAttribute("joursRetard", joursRetard);
                model.addAttribute("penalite", saved.getPenalite());
            }

            model.addAttribute("clientId", clientId);
            return "client/retours/success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/machines/error";
        }
    }
}