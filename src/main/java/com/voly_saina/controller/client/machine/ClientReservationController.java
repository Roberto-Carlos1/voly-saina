package com.voly_saina.controller.client.machine;

import com.voly_saina.entity.Machine;
import com.voly_saina.entity.ReservationMachine;
import com.voly_saina.entity.RetourMachine;
import com.voly_saina.entity.Utilisateur;
import com.voly_saina.entity.EtatMachine;
import com.voly_saina.service.EtatMachineService;
import com.voly_saina.service.MachineService;
import com.voly_saina.service.ReservationMachineService;
import com.voly_saina.service.RetourMachineService;
import com.voly_saina.service.StatutReservationService;
import com.voly_saina.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/client/reservations")
public class ClientReservationController {

    @Autowired
    private MachineService machineService;
    
    @Autowired
    private ReservationMachineService reservationService;
    
    @Autowired
    private StatutReservationService statutReservationService;
    
    @Autowired
    private EtatMachineService etatMachineService;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private RetourMachineService retourService;

    // ==================== FORMULAIRES ====================

    @GetMapping("/{machineId}/nouvelle")
    public String formulaireReservation(@PathVariable Long machineId,
                                        @RequestParam(required = false) Long clientId,
                                        Model model) {
        Machine machine = machineService.findById(machineId);
        if (machine == null || !"disponible".equals(machine.getEtatMachine().getCode())) {
            model.addAttribute("error", "Machine non disponible");
            return "client/machines/error";
        }
        model.addAttribute("machine", machine);
        model.addAttribute("clientId", clientId != null ? clientId : 1L);
        return "client/reservations/form";
    }

    @GetMapping("/{id}/annuler")
    public String formulaireAnnulation(@PathVariable Long id,
                                       @RequestParam(required = false) Long clientId,
                                       Model model) {
        Long idClient = clientId != null ? clientId : 1L;
        Utilisateur client = utilisateurService.findById(idClient)
            .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            
        ReservationMachine reservation = reservationService.findById(id)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        if (!reservation.getClient().getIdUtilisateur().equals(client.getIdUtilisateur())) {
            model.addAttribute("error", "Vous n'êtes pas autorisé");
            return "client/machines/error";
        }
        
        String statut = reservation.getStatutReservation().getCode();
        if ("terminee".equals(statut) || "annulee".equals(statut)) {
            model.addAttribute("error", "Cette réservation ne peut plus être annulée");
            return "client/machines/error";
        }
        
        model.addAttribute("reservation", reservation);
        model.addAttribute("clientId", clientId);
        return "client/reservations/annuler-form";
    }

    // ==================== ACTIONS ====================

    @PostMapping("/{machineId}/nouvelle")
    public String creerReservation(@PathVariable Long machineId,
                                   @RequestParam LocalDate dateDebut,
                                   @RequestParam LocalDate dateFin,
                                   @RequestParam(required = false) String lieuLivraison,
                                   @RequestParam(required = false) Long clientId,
                                   Model model) {
        try {
            Long idClient = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            
            Machine machine = machineService.findById(machineId);
            
            if (machine == null || !"disponible".equals(machine.getEtatMachine().getCode())) {
                model.addAttribute("error", "Machine non disponible");
                return "client/machines/error";
            }

            if (dateFin.isBefore(dateDebut)) {
                model.addAttribute("error", "Date de fin invalide");
                return "client/reservations/form";
            }

            ReservationMachine reservation = new ReservationMachine();
            reservation.setMachine(machine);
            reservation.setClient(client);
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setLieuLivraison(lieuLivraison);

            long jours = ChronoUnit.DAYS.between(dateDebut, dateFin);
            if (jours == 0) jours = 1;
            reservation.setPrixTotal(machine.getPrixJour().multiply(BigDecimal.valueOf(jours)));

            reservation.setStatutReservation(statutReservationService.findByCode("en_attente"));

            ReservationMachine saved = reservationService.save(reservation);

            model.addAttribute("reservation", saved);
            model.addAttribute("machine", machine);
            model.addAttribute("clientId", clientId);
            return "client/reservations/success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/machines/error";
        }
    }

    @PostMapping("/{id}/annuler")
    public String annulerReservation(@PathVariable Long id,
                                     @RequestParam(required = false) String motif,
                                     @RequestParam(required = false) Long clientId,
                                     Model model) {
        try {
            Long idClient = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            
            ReservationMachine reservation = reservationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

            if (!reservation.getClient().getIdUtilisateur().equals(client.getIdUtilisateur())) {
                model.addAttribute("error", "Vous n'êtes pas autorisé");
                return "client/machines/error";
            }

            String statut = reservation.getStatutReservation().getCode();
            if ("terminee".equals(statut) || "annulee".equals(statut)) {
                model.addAttribute("error", "Cette réservation ne peut plus être annulée");
                return "client/machines/error";
            }

            reservation.setStatutReservation(statutReservationService.findByCode("annulee"));
            if (motif != null) reservation.setMotifRefus(motif);
            reservationService.save(reservation);

            Machine machine = reservation.getMachine();
            if ("louee".equals(machine.getEtatMachine().getCode())) {
                EtatMachine disponible = etatMachineService.findById(1L);
                machine.getStatutMachine().setEtatMachine(disponible);
                machineService.save(machine);
            }

            model.addAttribute("reservation", reservation);
            model.addAttribute("message", "Réservation annulée avec succès");
            model.addAttribute("clientId", clientId);
            return "client/reservations/annuler-success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/machines/error";
        }
    }

    @PostMapping("/annuler-tout")
    public String annulerTout(@RequestParam(required = false) Long clientId, Model model) {
        try {
            Long idClient = clientId != null ? clientId : 1L;
            Utilisateur client = utilisateurService.findById(idClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));
            Long clientIdFinal = client.getIdUtilisateur();
            
            List<ReservationMachine> reservations = reservationService.findActiveReservationsByClient(clientIdFinal);
            
            int annulees = 0;
            for (ReservationMachine r : reservations) {
                String statut = r.getStatutReservation().getCode();
                if (!"terminee".equals(statut) && !"annulee".equals(statut)) {
                    r.setStatutReservation(statutReservationService.findByCode("annulee"));
                    reservationService.save(r);
                    annulees++;
                }
            }

            model.addAttribute("total", reservations.size());
            model.addAttribute("annulees", annulees);
            model.addAttribute("message", annulees + " réservation(s) annulée(s)");
            model.addAttribute("clientId", clientId);
            return "client/reservations/annuler-tout-success";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "client/machines/error";
        }
    }

    // ==================== CONSULTATION ====================

    @GetMapping("/mes-reservations")
    public String mesReservations(@RequestParam(required = false) Long clientId, Model model) {
        Long idClient = clientId != null ? clientId : 1L;
        Utilisateur client = utilisateurService.findById(idClient)
            .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        Long clientIdFinal = client.getIdUtilisateur();
        
        List<ReservationMachine> reservations = reservationService.findByClientId(clientIdFinal);
        
        BigDecimal totalPenalites = reservations.stream()
            .map(ReservationMachine::getRetour)
            .filter(r -> r != null && r.getPenalite() != null)
            .map(RetourMachine::getPenalite)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("reservations", reservations);
        model.addAttribute("totalPenalites", totalPenalites);
        model.addAttribute("client", client);
        model.addAttribute("clientId", clientId);
        return "client/reservations/list";
    }

    @GetMapping("/{id}")
    public String detailReservation(@PathVariable Long id,
                                    @RequestParam(required = false) Long clientId,
                                    Model model) {
        Long idClient = clientId != null ? clientId : 1L;
        Utilisateur client = utilisateurService.findById(idClient)
            .orElseThrow(() -> new RuntimeException("Client non trouvé"));
        
        ReservationMachine reservation = reservationService.findById(id)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        if (!reservation.getClient().getIdUtilisateur().equals(client.getIdUtilisateur())) {
            model.addAttribute("error", "Vous n'êtes pas autorisé");
            return "client/machines/error";
        }
        
        model.addAttribute("reservation", reservation);
        model.addAttribute("retour", retourService.findByReservation(id));
        model.addAttribute("clientId", clientId);
        return "client/reservations/detail";
    }
}